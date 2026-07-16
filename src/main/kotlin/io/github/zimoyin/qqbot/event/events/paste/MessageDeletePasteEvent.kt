package io.github.zimoyin.qqbot.event.events.paste

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.message.MessageDeletePasteHandler
import io.github.zimoyin.qqbot.net.bean.message.MessageReaction

/**
 * 消息表态移除事件（MESSAGE_REACTION_REMOVE）。
 *
 * 当用户取消对某条消息的表态（Emoji Reaction）时触发。
 * 对应 QQ Bot API 的 {@code MESSAGE_REACTION_REMOVE} 事件类型。
 *
 * 此事件是 {@link MessagePasteEvent} 接口的 {@code data class} 具体实现。
 * 与 {@link MessageAddPasteEvent} 构成表态添加/移除的完整生命周期。
 *
 * **典型使用场景：**
 * - 更新消息的表情反馈计数（减少计数）
 * - 取消与表态关联的业务逻辑
 * - 记录用户取消表态的行为日志
 *
 * @param metadata 事件元数据，通常包含服务器返回的完整 JSON 载荷
 * @param metadataType 事件类型标识符，固定为 "MESSAGE_REACTION_REMOVE"
 * @param botInfo 当前事件所属的机器人实例信息
 * @param message 表态信息实体，包含目标消息ID、被移除的表情类型、操作用户等数据
 * @param eventID 事件唯一标识符
 * @author : zimo
 * @date : 2023/12/20
 * @see MessagePasteEvent
 * @see MessageAddPasteEvent
 */
@EventAnnotation.EventMetaType("MESSAGE_REACTION_REMOVE")
@EventAnnotation.EventHandler(MessageDeletePasteHandler::class)
data class MessageDeletePasteEvent(
    override val metadata: String,
    override val metadataType: String = "MESSAGE_REACTION_REMOVE",
    override val botInfo: BotInfo,
    override val message: MessageReaction,
    override val eventID: String ="",
) : MessagePasteEvent
