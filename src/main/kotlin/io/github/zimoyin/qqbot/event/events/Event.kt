package io.github.zimoyin.qqbot.event.events

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.Bot
import io.github.zimoyin.qqbot.bot.BotInfo
import java.io.Serializable

/**
 * 事件根接口。
 *
 * 系统中所有事件的顶层抽象，定义了每个事件必须包含的基础字段和行为。
 * 所有具体事件（消息事件、频道事件、好友事件等）均直接或间接实现此接口。
 *
 * 注意：该接口实现了 [Serializable]，支持序列化传输，但在跨集群/跨进程传输时，
 * 不应序列化 [getBot] 返回的 Bot 实例，因为它包含 vertx 实例等不可序列化的上下文信息。
 *
 * @author : zimo
 * @date : 2023/12/06/17:42
 */
@EventAnnotation.EventMetaType("Not_MetaType_Event")
@EventAnnotation.EventHandler
interface Event : Serializable {
    /**
     * 事件的元数据/原始数据。
     *
     * 存储事件对应的原始 JSON 数据或序列化后的原始载荷，
     * 可用于调试、审计以及自定义数据提取场景。
     */
    val metadata: String

    /**
     * 事件类型标识符。
     *
     * 用于标识事件的类型，例如 "MESSAGE_CREATE"（消息创建）、
     * "GROUP_AT_MESSAGE_CREATE"（群@消息）、"INTERACTION_CREATE"（互动事件）等。
     * 由 [EventAnnotation.EventMetaType] 注解声明，子类覆盖此值以声明自身类型。
     */
    val metadataType: String

    /**
     * 机器人实例信息。
     *
     * 包含当前事件所属 Bot 的标识信息（如 appID、token 等）。
     * 该信息仅用于标识和鉴权，不包含运行时上下文，可在集群间安全传输。
     */
    val botInfo: BotInfo

    /**
     * 事件的唯一标识符。
     *
     * 由 QQ 服务器下发的事件 ID，用于去重和追踪。
     * 默认实现返回空字符串，子类在构造时应传入实际值。
     */
    val eventID: String
        get() = ""

    /**
     * 事件在本地创建时的时间戳（毫秒）。
     *
     * 采用 [System.currentTimeMillis] 获取，
     * 记录事件对象在本地 JVM 中创建的时刻，不受服务器时间偏差影响。
     */
    val localTimestamp: Long
        get() = System.currentTimeMillis()

    /**
     * 获取当前事件所属的 Bot 实例。
     *
     * 通过 [botInfo.token.appID] 从本地 Bot 注册表中查找对应的 [Bot] 实例。
     *
     * **重要提示：**
     * - Bot 实例仅在当前 JVM 进程中有效，严禁在集群/跨进程中传输或访问。
     * - Bot 内部持有 vertx 实例、WebSocket 连接以及其他运行时上下文，
     *   在远端反序列化后将无法正常工作或产生不可预期的行为。
     *
     * @return 当前事件对应的 [Bot] 实例
     * @throws NullPointerException 如果 Bot 实例未在本地注册表中找到
     */
    fun getBot(): Bot {
        return Bot.get(botInfo.token.appID)
            ?: throw NullPointerException("Bot is null. Every bot is private, you should not read it in other programs")
    }
}
