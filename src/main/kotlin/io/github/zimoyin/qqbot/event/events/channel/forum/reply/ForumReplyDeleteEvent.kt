package io.github.zimoyin.qqbot.event.events.channel.forum.reply

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.channel.forum.reply.ForumReplyDeleteHandler
import io.github.zimoyin.qqbot.net.bean.ForumReply

/**
 * 论坛回复删除事件。
 *
 * 当论坛内的某个回复被删除时触发此事件，对应的 EventType 为 `FORUM_REPLY_DELETE`。
 * 该事件携带被删除回复的完整信息，可通过 [forum] 获取被删除回复的各项属性。
 *
 * 常见触发场景：回复作者删除自己的回复、管理员删除违规回复等。
 *
 * @param metadata 事件的原始元数据字符串（JSON 格式），包含事件类型等基础信息
 * @param botInfo 当前接收到该事件的机器人实例信息，包含机器人的身份标识和连接会话
 * @param metadataType 事件元数据类型标识，固定值为 `"FORUM_REPLY_DELETE"`，用于区分不同事件类型
 * @param forum 被删除的论坛回复信息对象，包含回复ID、内容、作者ID、所属帖子ID等完整属性
 * @param eventID 事件的唯一标识符，用于去重和追踪，默认为空字符串
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * @see ForumReplyEvent 论坛回复事件基类
 */
@EventAnnotation.EventMetaType("FORUM_REPLY_DELETE")
@EventAnnotation.EventHandler(ForumReplyDeleteHandler::class)
data class ForumReplyDeleteEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val metadataType: String = "FORUM_REPLY_DELETE",
    override val forum: ForumReply,
    override val eventID: String ="",
) : ForumReplyEvent
