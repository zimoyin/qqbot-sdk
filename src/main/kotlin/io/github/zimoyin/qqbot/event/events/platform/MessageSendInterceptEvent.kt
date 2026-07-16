package io.github.zimoyin.qqbot.event.events.platform

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Contact
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler

/**
 * 消息被拦截事件。
 *
 * 当消息发送被拦截器阻止时触发此事件。此事件为**全局事件**，仅在全局事件总线中传递。
 *
 * 此事件通常由 {@link MessageSendPreEvent} 的拦截器机制触发——当任一拦截器
 * 将 {@code intercept} 属性设置为 {@code true} 后，框架会构造并发布此事件，
 * 通知所有监听器某条消息已被阻止发送。
 *
 * **使用场景：**
 * - 记录被拦截消息的日志或审计信息
 * - 向消息发送者返回"消息未通过审核"等提示信息
 * - 统计和监控消息拦截情况
 *
 * @param msgID 被拦截消息的唯一标识符（消息ID）
 * @param messageChain 被拦截的消息内容链
 * @param contact 消息发送的目标联系人/会话
 * @param botInfo 当前事件所属的机器人实例信息
 * @param eventID 事件唯一标识符
 * @author : zimo
 * @date : 2023/12/19
 * @see MessageSendPreEvent
 */
@EventAnnotation.EventMetaType("Platform_MessageSendInterceptEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
class MessageSendInterceptEvent(
    override val metadata: String = "Platform_MessageSendInterceptEvent",
    override val metadataType: String = "Platform_MessageSendInterceptEvent",
    /**
     * 被拦截消息的唯一标识符（消息ID）。
     */
    val msgID: String,
    /**
     * 被拦截的消息内容链。
     */
    val messageChain: MessageChain,
    /**
     * 消息发送的目标联系人/会话。
     */
    val contact: Contact,
    override val botInfo: BotInfo = contact.botInfo,
    override val eventID: String ="",
) : PlatformEvent
