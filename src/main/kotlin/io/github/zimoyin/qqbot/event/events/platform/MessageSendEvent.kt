package io.github.zimoyin.qqbot.event.events.platform

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Contact
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler
import io.github.zimoyin.qqbot.net.bean.SendMessageResultBean

/**
 * 消息发送事件。
 *
 * 当消息准备向 QQ 服务器推送时触发该事件。此事件为**全局事件**，
 * 仅在全局事件总线中传递，Bot 本地监听器无法捕获。
 *
 * **重要行为说明：**
 * - 该事件仅作为一个**通知**，表示消息即将被发送。**不建议**在此事件中尝试拦截或修改待发送的消息内容。
 * - 如需在消息发送前进行拦截或审核，请使用 {@link MessageSendPreEvent} 及其
 *   {@link MessageSendPreEvent#interceptor} 拦截器机制。
 * - 该事件为 {@code open class}，可被子类继承以针对不同场景（频道、群、好友）提供特化事件。
 *
 * @param msgID 待发送消息的唯一标识符（消息ID）
 * @param messageChain 待发送的消息内容链，包含文本、图片、表情等多种消息元素的组合
 * @param result 服务器返回的发送结果信息，包含发送状态、消息序号等
 * @param contact 消息发送的目标联系人/会话（可能是群组、频道、好友等）
 * @param botInfo 当前事件所属的机器人实例信息
 * @param eventID 事件唯一标识符
 * @author : zimo
 * @date : 2023/12/19
 * @see MessageSendPreEvent
 * @see PlatformEvent
 */
@EventAnnotation.EventMetaType("Platform_MessageSendEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
open class MessageSendEvent(
    override val metadata: String = "Platform_MessageSendEvent",
    override val metadataType: String = "Platform_MessageSendEvent",
    /**
     * 待发送消息的唯一标识符（消息ID）。
     */
    open val msgID: String,
    /**
     * 待发送的消息内容链。
     */
    open val messageChain: MessageChain,
    /**
     * 服务器返回的发送结果信息，包含发送状态等数据。
     */
    open val result: SendMessageResultBean,
    /**
     * 消息发送的目标联系人/会话。
     */
    open val contact: Contact,
    override val botInfo: BotInfo = contact.botInfo,
    override val eventID: String ="",
) : PlatformEvent
