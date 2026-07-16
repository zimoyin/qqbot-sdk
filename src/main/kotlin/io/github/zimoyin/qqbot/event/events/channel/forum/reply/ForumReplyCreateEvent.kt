package io.github.zimoyin.qqbot.event.events.channel.forum.reply

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.channel.forum.reply.ForumReplyCreateHandler
import io.github.zimoyin.qqbot.net.bean.ForumReply

/**
 * 论坛回复创建事件。
 *
 * 当用户在论坛帖子下发布新的回复/评论时触发此事件，对应的 EventType 为 `FORUM_REPLY_CREATE`。
 * 该事件携带新创建回复的完整信息，可通过 [forum] 获取回复内容、作者、所属帖子等属性。
 *
 * @param metadata 事件的原始元数据字符串（JSON 格式），包含事件类型等基础信息
 * @param botInfo 当前接收到该事件的机器人实例信息，包含机器人的身份标识和连接会话
 * @param metadataType 事件元数据类型标识，固定值为 `"FORUM_REPLY_CREATE"`，用于区分不同事件类型
 * @param forum 新创建的论坛回复信息对象，包含回复ID、内容、作者ID、所属帖子ID等完整属性
 * @param eventID 事件的唯一标识符，用于去重和追踪，默认为空字符串
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * @see ForumReplyEvent 论坛回复事件基类
 */
@EventAnnotation.EventMetaType("FORUM_REPLY_CREATE")
@EventAnnotation.EventHandler(ForumReplyCreateHandler::class)
data class ForumReplyCreateEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val metadataType: String = "FORUM_REPLY_CREATE",
    override val forum: ForumReply,
    override val eventID: String ="",
) : ForumReplyEvent
