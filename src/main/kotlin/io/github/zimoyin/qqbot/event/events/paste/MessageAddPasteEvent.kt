package io.github.zimoyin.qqbot.event.events.paste

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.message.MessageAddPasteHandler
import io.github.zimoyin.qqbot.net.bean.message.MessageReaction

/**
 * 消息表态添加事件（MESSAGE_REACTION_ADD）。
 *
 * 当用户对某条消息添加表态（Emoji Reaction）时触发。
 * 对应 QQ Bot API 的 {@code MESSAGE_REACTION_ADD} 事件类型。
 *
 * 此事件是 {@link MessagePasteEvent} 接口的 {@code data class} 具体实现。
 *
 * **典型使用场景：**
 * - 统计消息的表情反馈情况
 * - 根据用户表态触发相应的业务逻辑（如点赞计数、表情投票等）
 * - 记录用户交互行为日志
 *
 * @param metadata 事件元数据，通常包含服务器返回的完整 JSON 载荷
 * @param metadataType 事件类型标识符，固定为 "MESSAGE_REACTION_ADD"
 * @param botInfo 当前事件所属的机器人实例信息
 * @param message 表态信息实体，包含目标消息ID、表情类型、操作用户等数据
 * @param eventID 事件唯一标识符
 * @author : zimo
 * @date : 2023/12/20
 * @see MessagePasteEvent
 * @see MessageDeletePasteEvent
 */
@EventAnnotation.EventMetaType("MESSAGE_REACTION_ADD")
@EventAnnotation.EventHandler(MessageAddPasteHandler::class)
data class MessageAddPasteEvent(
    override val metadata: String,
    override val metadataType: String = "MESSAGE_REACTION_ADD",
    override val botInfo: BotInfo,
    override val message: MessageReaction,
    override val eventID: String ="",
) : MessagePasteEvent
