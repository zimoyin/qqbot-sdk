package io.github.zimoyin.qqbot.event.events.channel.forum.post

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.channel.forum.post.ForumPostDeleteHandler
import io.github.zimoyin.qqbot.net.bean.ForumPost


/**
 * 论坛帖子删除事件。
 *
 * 当论坛内的某个帖子（评论）被删除时触发此事件，对应的 EventType 为 `FORUM_POST_DELETE`。
 * 该事件携带被删除帖子的完整信息，可通过 [forum] 获取被删除帖子的各项属性。
 *
 * 常见触发场景：帖子作者删除自己的评论、管理员删除违规评论等。
 *
 * @param metadata 事件的原始元数据字符串（JSON 格式），包含事件类型等基础信息
 * @param botInfo 当前接收到该事件的机器人实例信息，包含机器人的身份标识和连接会话
 * @param metadataType 事件元数据类型标识，固定值为 `"FORUM_POST_DELETE"`，用于区分不同事件类型
 * @param forum 被删除的论坛帖子信息对象，包含帖子ID、内容、作者ID、所属主题ID等完整属性
 * @param eventID 事件的唯一标识符，用于去重和追踪，默认为空字符串
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * @see ForumPostEvent 论坛帖子事件基类
 */
@EventAnnotation.EventMetaType("FORUM_POST_DELETE")
@EventAnnotation.EventHandler(ForumPostDeleteHandler::class)
data class ForumPostDeleteEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val metadataType: String = "FORUM_POST_DELETE",
    override val forum: ForumPost,
    override val eventID: String ="",
) : ForumPostEvent
