package io.github.zimoyin.qqbot.event.events.revoke

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Contact
import io.github.zimoyin.qqbot.bot.contact.User
import io.github.zimoyin.qqbot.event.handler.message.ChannelMessageRevokeHandler

/**
 * 频道消息撤回事件（MESSAGE_DELETE）。
 *
 * 当频道中的公开消息被撤回时触发。对应 QQ Bot API 的 {@code MESSAGE_DELETE} 事件类型。
 *
 * 此事件是 {@link MessageRevokeEvent} 接口的 {@code data class} 具体实现。
 *
 * **典型使用场景：**
 * - 记录被撤回消息的日志（用于审计或追溯）
 * - 同步删除数据库中已存储的消息记录
 * - 通知其他系统消息已被撤回
 *
 * @param metadata 事件元数据，通常包含服务器返回的完整 JSON 载荷
 * @param botInfo 当前事件所属的机器人实例信息
 * @param msgID 被撤回消息的唯一标识符（消息ID）
 * @param windows 被撤回消息所在的会话窗口/频道
 * @param sender 原消息的发送者
 * @param metadataType 事件类型标识符，固定为 "MESSAGE_DELETE"
 * @param operatorID 执行撤回操作的用户ID
 * @param eventID 事件唯一标识符
 * @author : zimo
 * @date : 2023/12/20
 * @see MessageRevokeEvent
 */
@EventAnnotation.EventMetaType("MESSAGE_DELETE")
@EventAnnotation.EventHandler(ChannelMessageRevokeHandler::class, true)
data class ChannelMessageRevokeEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val msgID: String,
    override val windows: Contact,
    override val sender: User,
    override val metadataType: String = "MESSAGE_DELETE",
    override val operatorID: String,
    override val eventID: String ="",
) : MessageRevokeEvent
