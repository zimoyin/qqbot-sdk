package io.github.zimoyin.qqbot.event.events.revoke

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Contact
import io.github.zimoyin.qqbot.bot.contact.User
import io.github.zimoyin.qqbot.event.handler.message.ChannelPrivateMessageRevokeHandler

/**
 * 频道私信消息撤回事件（DIRECT_MESSAGE_DELETE）。
 *
 * 当频道中的私信消息被撤回时触发。对应 QQ Bot API 的 {@code DIRECT_MESSAGE_DELETE} 事件类型。
 *
 * 此事件是 {@link MessageRevokeEvent} 接口的 {@code data class} 具体实现，
 * 与 {@link ChannelMessageRevokeEvent} 的区别在于：
 * - {@code ChannelMessageRevokeEvent}：频道公开频道的消息撤回
 * - {@code ChannelPrivateMessageRevokeEvent}：频道私信（Direct Message）的消息撤回
 *
 * **典型使用场景：**
 * - 记录被撤回私信的日志
 * - 同步删除数据库中已存储的私信记录
 *
 * @param metadata 事件元数据，通常包含服务器返回的完整 JSON 载荷
 * @param botInfo 当前事件所属的机器人实例信息
 * @param msgID 被撤回消息的唯一标识符（消息ID）
 * @param windows 被撤回消息所在的私信会话窗口
 * @param sender 原消息的发送者
 * @param metadataType 事件类型标识符，固定为 "DIRECT_MESSAGE_DELETE"
 * @param operatorID 执行撤回操作的用户ID
 * @param eventID 事件唯一标识符
 * @author : zimo
 * @date : 2023/12/20
 * @see MessageRevokeEvent
 * @see ChannelMessageRevokeEvent
 */
@EventAnnotation.EventMetaType("DIRECT_MESSAGE_DELETE")
@EventAnnotation.EventHandler(ChannelPrivateMessageRevokeHandler::class, true)
data class ChannelPrivateMessageRevokeEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val msgID: String,
    override val windows: Contact,
    override val sender: User,
    override val metadataType: String = "DIRECT_MESSAGE_DELETE",
    override val operatorID: String,
    override val eventID: String ="",
) : MessageRevokeEvent
