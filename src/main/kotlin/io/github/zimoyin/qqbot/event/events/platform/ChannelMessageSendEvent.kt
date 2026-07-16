package io.github.zimoyin.qqbot.event.events.platform

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler
import io.github.zimoyin.qqbot.net.bean.SendMessageResultBean

/**
 * 频道消息发送事件（{@link MessageSendEvent} 的频道特化子类）。
 *
 * 当消息准备向频道服务器推送时触发。此事件为**全局事件**，仅在全局事件总线中传递。
 *
 * 该事件是 {@link MessageSendEvent} 的 {@code data class} 具体实现，
 * 将 {@code contact} 类型特化为 {@link io.github.zimoyin.qqbot.bot.contact.Channel}。
 *
 * **重要行为说明：**
 * - 该事件仅作为通知，不建议在此事件中拦截或修改待发送的消息。
 * - 如需审核频道消息，请使用 {@link ChannelMessageSendPreEvent} 配合
 *   {@link MessageSendPreEvent#interceptor} 拦截器机制。
 *
 * @param msgID 待发送消息的唯一标识符（消息ID）
 * @param messageChain 待发送的消息内容链
 * @param result 服务器返回的发送结果信息
 * @param contact 目标频道
 * @param botInfo 当前事件所属的机器人实例信息
 * @author : zimo
 * @date : 2023/12/19
 * @see MessageSendEvent
 * @see ChannelMessageSendPreEvent
 */
@EventAnnotation.EventMetaType("Platform_ChannelMessageSendEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
data class ChannelMessageSendEvent(
    override val metadata: String = "Platform_ChannelMessageSendEvent",
    override val metadataType: String = "Platform_ChannelMessageSendEvent",
    override val contact: Channel,
    override val botInfo: BotInfo = contact.botInfo,
    override val msgID: String,
    override val messageChain: MessageChain,
    override val result: SendMessageResultBean,
) : MessageSendEvent(
    metadata = metadata,
    metadataType = metadataType,
    msgID = msgID,
    messageChain = messageChain,
    contact = contact,
    botInfo = botInfo,
    result = result
)
