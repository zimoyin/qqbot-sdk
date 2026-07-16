package io.github.zimoyin.qqbot.event.events.platform

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Friend
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler

/**
 * 好友消息发送前事件（{@link MessageSendPreEvent} 的好友私聊特化子类）。
 *
 * 在好友消息**发送到服务器之前**触发。此事件为**全局事件**，仅在全局事件总线中传递。
 *
 * 该事件是 {@link MessageSendPreEvent} 的 {@code data class} 具体实现，
 * 将 {@code contact} 类型特化为 {@link io.github.zimoyin.qqbot.bot.contact.Friend}。
 *
 * **重要行为说明：**
 * - 不要直接在此事件中修改消息内容来进行审核拦截，
 *   请通过 {@link MessageSendPreEvent#interceptor} 静态方法注册拦截器。
 * - 拦截器可以对 {@code messageChain} 进行修改，并通过设置 {@code intercept = true} 来阻止消息发送。
 *
 * @param msgID 待发送消息的唯一标识符（消息ID）
 * @param messageChain 待发送的消息内容链，可在拦截器中修改
 * @param contact 目标好友
 * @param botInfo 当前事件所属的机器人实例信息
 * @param eventID 事件唯一标识符
 * @author : zimo
 * @date : 2023/12/19
 * @see MessageSendPreEvent
 * @see FriendMessageSendEvent
 */
@EventAnnotation.EventMetaType("Platform_FriendMessageSendPreEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
data class FriendMessageSendPreEvent(
    override val metadata: String = "Platform_FriendMessageSendPreEvent",
    override val metadataType: String = "Platform_FriendMessageSendPreEvent",
    override val contact: Friend,
    override val botInfo: BotInfo = contact.botInfo,
    override val msgID: String,
    override var messageChain: MessageChain,
    override val eventID: String ="",
) : MessageSendPreEvent(
    metadata = metadata,
    metadataType = metadataType,
    msgID = msgID,
    messageChain = messageChain,
    contact = contact,
    botInfo = botInfo,
)
