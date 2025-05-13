package io.github.zimoyin.qqbot.bot.contact

import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.net.bean.SendMessageResultBean
import io.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import io.github.zimoyin.qqbot.net.http.api.friend.recallFriendMessage
import io.github.zimoyin.qqbot.net.http.api.friend.sendFriendMessage
import io.vertx.core.Future

/**
 * 好友
 * @author : zimo
 * @date : 2024/03/31
 */
interface Friend : Contact {
    /**
     * 好友ID & openid
     */
    override val id: String

    /**
     * 机器人信息
     */
    override val botInfo: BotInfo

    /**
     * 好友昵称
     */
    val nick: String

    /**
     * 头像
     */
    val avatar: String

    /**
     * 私聊信息撤回
     * 撤回和该朋友的聊天时，我发送的信息
     */
    @OptIn(UntestedApi::class)
    override fun recall(messageID: String): Future<Boolean> {
        return HttpAPIClient.recallFriendMessage(this, messageID)
    }

    @Deprecated("Official ban on active sending")
    override fun send(message: MessageChain): Future<SendMessageResultBean> {
        return HttpAPIClient.sendFriendMessage(this, message)
    }
}
