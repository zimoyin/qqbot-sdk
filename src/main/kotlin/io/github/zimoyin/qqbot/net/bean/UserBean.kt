package io.github.zimoyin.qqbot.net.bean

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.contact.ChannelPrivateUser
import io.vertx.core.json.JsonObject
import java.io.Serializable
import java.util.*

/**
 *
 * @author : zimo
 * @date : 2023/12/11
 */


/**
 * 用户对象
 */
data class User(
    /**
     * 在频道中的用户 id
     */
    @field:JsonProperty("id")
    val inChannelUserID: String? = null,

    /**
     * 在群聊中的用户 id
     */
    @field:JsonProperty("member_openid")
    val inGroupUserID: String? = null,

    /**
     * 在单独的用户聊天中的用户 id
     */
    @field:JsonProperty("user_openid")
    val inOpenUserID: String? = null,

    /**
     * 用户名
     */
    @field:JsonProperty("username")
    val username: String? = null,

    /**
     * 用户头像地址
     */
    @field:JsonProperty("avatar")
    val avatar: String? = null,

    /**
     * 是否是机器人
     */
    @field:JsonProperty("bot")
    val isBot: Boolean? = null,

    /**
     * 特殊关联应用的 openid
     * 需要特殊申请并配置后才会返回
     */
    @field:JsonProperty("union_openid")
    val unionOpenID: String? = null,

    /**
     * 机器人关联的互联应用的用户信息
     * 与 union_openid 关联的应用是同一个
     */
    @field:JsonProperty("union_user_account")
    val unionUserAccount: String? = null,
) : Serializable {
    /**
     * 获取ID，在哪个场景下就获取哪个ID
     */
    @get:Throws(NullPointerException::class)
    val uid: String by lazy {
        inGroupUserID ?: inOpenUserID ?: inChannelUserID ?: throw NullPointerException("user ID is null")
    }

    /**
     * 获取 原始 ID 字段
     */
    @get:Throws(NullPointerException::class)
    val originalID: String by lazy {
        inChannelUserID ?: throw NullPointerException("user ID is null")
    }
}


/**
 * 成员对象
 */
data class MemberBean(
    /**
     * 用户的频道基础信息
     * 只有成员相关接口中会填充此信息
     */
    @field:JsonProperty("user")
    val user: User? = null,

    /**
     * 用户的昵称
     */
    @field:JsonProperty("nick")
    val nick: String? = null,

    /**
     * 用户在频道内的身份组ID, 默认值可参考DefaultRoles
     */
    @field:JsonProperty("roles")
    val roles: List<String>? = null,

    /**
     * 用户加入频道的时间
     */
    @field:JsonProperty("joined_at")
    val joinedAt: Date? = null,
) : Serializable {

    /**
     * 将成员对象转换为频道用户对象
     */
    @JsonIgnore
    fun mapToChannelUser(channel: Channel): ChannelPrivateUser {
        return ChannelPrivateUser(
            id = user!!.uid,
            nick = nick ?: "",
            isBot = user.isBot ?: false,
            avatar = user.avatar ?: "",
            roles = roles ?: emptyList(),
            joinedAt = joinedAt!!,
            unionOpenID = user.unionOpenID,
            unionUserAccount = user.unionUserAccount,
            botInfo = channel.botInfo,
            channel = channel
        )
    }
}

/**
 * 带有频道id的成员对象
 */
data class MemberWithGuildID(
    /**
     * 频道id
     */
    @field:JsonProperty("guild_id")
    val guildID: String? = null,

    /**
     * 用户的频道基础信息
     */
    @field:JsonProperty("user")
    val user: User? = null,

    /**
     * 用户的昵称
     */
    @field:JsonProperty("nick")
    val nick: String? = null,

    /**
     * 用户在频道内的身份
     */
    @field:JsonProperty("roles")
    val roles: List<String>? = null,

    /**
     * 用户加入频道的时间
     */
    @field:JsonProperty("joined_at")
    val joinedAt: Date? = null,

    /**
     * 操作人ID
     * 当事件发生时该字段才会填充
     */
    @field:JsonProperty("op_user_id")
    val opUserID: String? = null,

    @field:JsonProperty("source_type")
    val sourceType: String? = null,
) : Serializable


//@JsonIgnoreProperties(ignoreUnknown = true)
data class BotUser(
    val id: String = "",
    val username: String = "",
    val bot: Boolean = true,
    val status: Int = 0,
    val avatar: String? = null,
    @field:JsonProperty("union_openid")
    val unionOpenID: String? = null,
    @field:JsonProperty("union_user_account")
    val unionUserAccount: String? = null,
    @field:JsonProperty("share_url")
    val shareUrl: String? = null,
    @field:JsonProperty("welcome_msg")
    val welcomeMsg: String? = null,
    val message: String = "success",
    val code: Int = 0,
    @field:JsonProperty("err_code")
    val errCode: Int = 0,
    @field:JsonProperty("trace_id")
    val traceId: String? = null,
) : Serializable {
    fun errorMessage(): JsonObject = JsonObject().apply {
        put("message", message)
        put("code", code)
        put("err_code", errCode)
        put("trace_id", traceId)
    }
}


/**
 * 定义权限枚举，每个枚举项代表一种权限，并携带其对应的值
 */
enum class Permissions(val value: Int) : Serializable {
    /**
     * 可查看子频道
     * 序列化后为十进制1
     */
    PERMISSION_VIEW_SUB_CHANNEL(1 shl 0),

    /**
     * 可管理子频道
     * 序列化后为十进制2
     */
    PERMISSION_MANAGE_SUB_CHANNEL(1 shl 1),

    /**
     * 可发言子频道
     * 序列化后为十进制4
     */
    PERMISSION_SPEAK_SUB_CHANNEL(1 shl 2);
}

class ContactPermission(
    private var permission: Int = 0,
) : Serializable {
    constructor() : this(4)

    /**
     * 检查用户权限中是否包含指定权限
     * @param targetPermission 目标权限 ChannelPermission.PERMISSION_xxx
     */
    fun hasPermission(targetPermission: Permissions): Boolean {
        return (permission and targetPermission.value) == targetPermission.value
    }

    /**
     * 添加用户权限
     */
    fun addPermission(targetPermission: Permissions): ContactPermission {
        this.permission = this.permission or targetPermission.value
        return this
    }

    /**
     * 从用户权限中移除指定权限
     */
    fun removePermission(targetPermission: Permissions): Permissions {
        this.permission = this.permission and (targetPermission.value.inv())
        return targetPermission
    }

    fun getDifferenceSet(): ContactPermission {
        val FULL_PERMISSIONS = ContactPermission()
            .addPermission(Permissions.PERMISSION_SPEAK_SUB_CHANNEL)
            .addPermission(Permissions.PERMISSION_MANAGE_SUB_CHANNEL)
            .addPermission(Permissions.PERMISSION_VIEW_SUB_CHANNEL)
        return ContactPermission(permission = (FULL_PERMISSIONS.permission.inv() and this.permission) or (FULL_PERMISSIONS.permission and this.permission.inv()))
    }

    fun getPermission(): Int {
        return permission
    }

    /**
     * 检查用户是否有查看子频道权限
     */
    @get:JvmName("isViewSubChannel")
    val isViewSubChannel: Boolean
        get() = hasPermission(Permissions.PERMISSION_VIEW_SUB_CHANNEL)

    /**
     * 检查用户是否有管理子频道权限
     */
    @get:JvmName("isManageSubChannel")
    val isManageSubChannel: Boolean
        get() = hasPermission(Permissions.PERMISSION_MANAGE_SUB_CHANNEL)

    /**
     * 检查用户是否有发言子频道权限
     */
    @get:JvmName("isSpeakSubChannel")
    val isSpeakSubChannel: Boolean
        get() = hasPermission(Permissions.PERMISSION_SPEAK_SUB_CHANNEL)

    override fun toString(): String {
        return "UserPermission(permission=$permission, isViewSubChannel=$isViewSubChannel, isManageSubChannel=$isManageSubChannel, isSpeakSubChannel=$isSpeakSubChannel)"
    }
}
