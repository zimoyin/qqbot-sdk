package io.github.zimoyin.qqbot.bot.contact

import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.channel.ChannelImpl
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.net.bean.ContactPermission
import io.github.zimoyin.qqbot.net.bean.SendMessageResultBean
import io.github.zimoyin.qqbot.net.bean.message.Message
import io.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import io.github.zimoyin.qqbot.net.http.api.channel.*
import io.github.zimoyin.qqbot.net.http.api.friend.sendFriendMessage
import io.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future
import java.io.Serializable
import java.util.*

/**
 * 频道与群聊中抽象出的用户类
 * @author : zimo
 * @date : 2023/12/11
 */
interface User : Friend {
    /**
     * 用户id
     */
    override val id: String

    /**
     * 是否是官方API开发的机器人
     */
    val isBot: Boolean

    /**
     *  用户身份组id
     */
    val roles: List<String>

    /**
     *  用户加入频道的时间
     *  用户加入群聊的时间
     *  用户添加机器人的时间
     */
    val joinedAt: Date

    /**
     * 特殊关联应用的 openid
     * 需要特殊申请并配置后才会返回
     */
    val unionOpenID: String?

    /**
     * 机器人关联的互联应用的用户信息
     * 与 union_openid 关联的应用是同一个
     */
    val unionUserAccount: String?

    /**
     * 禁言用户
     */
    fun mute(
        muteTimestamp: Long = 24 * 60 * 1000,
        muteEndTimestamp: Long = System.currentTimeMillis() + muteTimestamp,
    ): Future<Boolean> {
        val promise = promise<Boolean>()
        promise.fail(IllegalStateException("该用户无法在此聊天会话下被禁言"))
        return promise.future()
    }
}

/**
 * 频道与群聊中抽象出的用户类，用于代表发送信息的用户
 */
open class Sender(
    override val id: String,
    override val nick: String,
    override val isBot: Boolean,
    override val avatar: String,
    override val roles: List<String>,
    override val joinedAt: Date,
    override val unionOpenID: String?,
    override val unionUserAccount: String?,
    override val botInfo: BotInfo,
) : User, Serializable {
    companion object {
        fun convert(
            botInfo: BotInfo, message: Message,
        ): Sender = Sender(
            id = message.author!!.uid,
            nick = message.author.username!!,
            isBot = message.author.isBot ?: false,
            avatar = message.author.avatar ?: "",
            roles = message.member?.roles ?: emptyList(),
            joinedAt = message.member?.joinedAt ?: Date(),
            unionOpenID = message.author.unionOpenID,
            unionUserAccount = message.author.unionUserAccount,
            botInfo = botInfo
        )
    }



    @Deprecated("Official ban on active sending")
    override fun send(message: MessageChain): Future<SendMessageResultBean> {
//        return HttpAPIClient.sendFriendMessage(this, message)
        // TODO 自动分别群组和频道，并向用户发起私聊
        TODO("暂时未能实现")
    }
}

/**
 * 私聊用户
 */
data class PrivateFriend(
    override val id: String,
    override val nick: String,
    override val isBot: Boolean,
    override val avatar: String,
    override val roles: List<String>,
    override val joinedAt: Date,
    override val unionOpenID: String?,
    override val unionUserAccount: String?,
    override val botInfo: BotInfo,
) : Sender(id, nick, isBot, avatar, roles, joinedAt, unionOpenID, unionUserAccount, botInfo){
    companion object {
        fun convert(
            botInfo: BotInfo, message: Message,
        ): PrivateFriend = PrivateFriend(
            id = message.author!!.uid,
            nick = message.author.username ?: "none",
            isBot = message.author.isBot ?: false,
            avatar = message.author.avatar ?: "none",
            roles = message.member?.roles ?: emptyList(),
            joinedAt = message.member?.joinedAt ?: Date(),
            unionOpenID = message.author.unionOpenID,
            unionUserAccount = message.author.unionUserAccount,
            botInfo = botInfo,
        )

        fun convert(
            botInfo: BotInfo,
            id:String,
            nick: String =  "none",
            avatar: String =  "none",
            isBot: Boolean = false,
            roles: List<String> = emptyList(),
            joinedAt: Date = Date(),
            unionOpenID: String? = null,
            unionUserAccount: String? = null,
        ): PrivateFriend = PrivateFriend(
            id = id,
            nick = nick,
            isBot = isBot,
            avatar = avatar,
            roles = roles,
            joinedAt = joinedAt,
            unionOpenID = unionOpenID,
            botInfo = botInfo,
            unionUserAccount = unionUserAccount,
        )
    }

    override fun send(message: MessageChain): Future<SendMessageResultBean> {
        return HttpAPIClient.sendFriendMessage(this, message)
    }
}


/**
 * 频道私聊用户
 */
data class ChannelPrivateUser(
    override val id: String,
    override val nick: String,
    override val isBot: Boolean,
    override val avatar: String,
    override val roles: List<String>,
    override val joinedAt: Date,
    override val unionOpenID: String?,
    override val unionUserAccount: String?,
    override val botInfo: BotInfo,
    val channel: Channel,
) : Sender(id, nick, isBot, avatar, roles, joinedAt, unionOpenID, unionUserAccount, botInfo) {
    companion object {
        fun convert(
            botInfo: BotInfo, message: Message,
        ): ChannelPrivateUser {
            return ChannelPrivateUser(
                id = message.author!!.uid,
                nick = message.author.username!!,
                isBot = message.author.isBot ?: false,
                avatar = message.author.avatar ?: "none",
                roles = message.member?.roles ?: emptyList(),
                joinedAt = message.member?.joinedAt ?: Date(),
                unionOpenID = message.author.unionOpenID,
                unionUserAccount = message.author.unionUserAccount,
                botInfo = botInfo,
                channel = ChannelImpl.convert(botInfo, message),
            )
        }
    }

    override fun send(message: MessageChain): Future<SendMessageResultBean> {
        val promise = promise<SendMessageResultBean>()
        if (!channel.isPrivateChat) {
            promise.fail("The current robot is not allowed to send messages to the channel and can only send messages to the current contact. [Channel unable to obtain temporary contact ID]")
            return promise.future()
        }
        return HttpAPIClient.sendChannelPrivateMessageAsync(channel, message)
    }

    override fun recall(messageID: String): Future<Boolean> {
        return channel.recall(messageID)
    }

    @JvmOverloads
    fun mute(muteTimestamp: Long = 24 * 60 * 1000): Future<Boolean> {
        return mute(muteTimestamp, System.currentTimeMillis() + muteTimestamp)
    }


    @OptIn(UntestedApi::class)
    override fun mute(muteTimestamp: Long, muteEndTimestamp: Long): Future<Boolean> {
        return HttpAPIClient.setChannelMuteMember(channel, id, muteTimestamp, muteEndTimestamp)
    }


    /**
     * 创建频道身份组成员
     * 用于将频道guild_id下的用户 user_id 添加到身份组 role_id
     *
     */
    @UntestedApi
    fun addGuildRoleMember(role: Role): Future<Role> {
        return HttpAPIClient.addGuildRoleMember(channel, id, role.id)
    }

    /**
     * 删除频道成员
     * @param userID 成员ID
     * @param addBlacklist 是否将用户加入黑名单
     * @param deleteHistoryMsg 是否删除用户的历史消息
     *
     */
    @UntestedApi
    @JvmOverloads
    fun deleteGuildMember(
        addBlacklist: Boolean = false,
        deleteHistoryMsg: MessageRevokeTimeRange = MessageRevokeTimeRange.NO_REVOKE,
    ): Future<Boolean> {
        return HttpAPIClient.deleteSubChannelMember(channel, id, addBlacklist, deleteHistoryMsg)
    }

    /**
     * 获取子频道用户权限
     */
    @OptIn(UntestedApi::class)
    fun getChannelPermissions(): Future<ContactPermission> {
        return HttpAPIClient.getChannelPermissions(channel, id)
    }

    /**
     * 修改子频道用户权限
     */
    @OptIn(UntestedApi::class)
    fun updateChannelPermissions(permissions: ContactPermission): Future<Boolean> {
        return HttpAPIClient.updateChannelPermissions(channel, id, permissions)
    }
}
