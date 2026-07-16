package io.github.zimoyin.qqbot.event.events.channel.forum.thread

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.channel.forum.thread.ForumThreadCreateHandler
import io.github.zimoyin.qqbot.net.bean.ForumThread


/**
 * 论坛主题创建事件。
 *
 * 当用户在论坛（话题子频道）内发布新的主题帖时触发此事件，对应的 EventType 为 `FORUM_THREAD_CREATE`。
 * 该事件携带新创建主题的完整信息，可通过 [forum] 获取主题标题、内容、作者等属性。
 *
 * 每个主题帖代表一个独立的讨论话题，其他用户可以在该主题下进行评论和回复。
 *
 * @param metadata 事件的原始元数据字符串（JSON 格式），包含事件类型等基础信息
 * @param botInfo 当前接收到该事件的机器人实例信息，包含机器人的身份标识和连接会话
 * @param metadataType 事件元数据类型标识，固定值为 `"FORUM_THREAD_CREATE"`，用于区分不同事件类型
 * @param forum 新创建的论坛主题信息对象，包含主题ID、标题、内容、作者ID等完整属性
 * @param eventID 事件的唯一标识符，用于去重和追踪，默认为空字符串
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * @see ForumThreadEvent 论坛主题事件基类
 */
@EventAnnotation.EventMetaType("FORUM_THREAD_CREATE")
@EventAnnotation.EventHandler(ForumThreadCreateHandler::class)
data class ForumThreadCreateEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val metadataType: String = "FORUM_THREAD_CREATE",
    override val forum: ForumThread,
    override val eventID: String ="",
) : ForumThreadEvent
