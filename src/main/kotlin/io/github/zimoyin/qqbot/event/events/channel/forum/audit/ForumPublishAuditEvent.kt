package io.github.zimoyin.qqbot.event.events.channel.forum.audit

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.contact.channel.ChannelImpl
import io.github.zimoyin.qqbot.event.events.channel.forum.ForumEvent
import io.github.zimoyin.qqbot.event.handler.channel.forum.audit.ForumPublishAuditHandler
import io.github.zimoyin.qqbot.net.bean.ForumAuditResult


/**
 * 论坛发布审核结果事件。
 *
 * 当频道内开启发帖审核功能后，用户在论坛发布的帖子经过审核系统审核后触发此事件，
 * 对应的 EventType 为 `FORUM_PUBLISH_AUDIT_RESULT`。
 * 该事件携带审核结果的完整信息，可通过 [forum] 获取审核状态、审核意见等属性。
 *
 * 该事件直接继承自 [ForumEvent]，而非 [ForumThreadEvent]、[ForumPostEvent] 或 [ForumReplyEvent]，
 * 因为审核结果事件涵盖的范围可能涉及主题帖、帖子、回复等多种内容类型。
 *
 * [channel] 属性会根据审核结果信息中的频道组ID和子频道ID自动构建。
 *
 * @param metadata 事件的原始元数据字符串（JSON 格式），包含事件类型等基础信息
 * @param botInfo 当前接收到该事件的机器人实例信息，包含机器人的身份标识和连接会话
 * @param metadataType 事件元数据类型标识，固定值为 `"FORUM_PUBLISH_AUDIT_RESULT"`，用于区分不同事件类型
 * @param eventID 事件的唯一标识符，用于去重和追踪，默认为空字符串
 * @param forum 论坛发布审核结果信息对象，包含审核状态、审核意见、被审核内容ID等完整属性
 * @param channel 事件所属的论坛子频道对象，根据审核信息中的频道组ID和子频道ID自动构建
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * @see ForumEvent 论坛事件基类
 */
@EventAnnotation.EventMetaType("FORUM_PUBLISH_AUDIT_RESULT")
@EventAnnotation.EventHandler(ForumPublishAuditHandler::class)
data class ForumPublishAuditEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val metadataType: String = "FORUM_PUBLISH_AUDIT_RESULT",
    override val eventID: String ="",
    val forum: ForumAuditResult,
    override val channel: Channel = ChannelImpl.convert(botInfo, forum.guildId!!, forum.channelId, forum.channelId),
) : ForumEvent
