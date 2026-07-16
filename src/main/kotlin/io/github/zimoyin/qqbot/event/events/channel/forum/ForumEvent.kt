package io.github.zimoyin.qqbot.event.events.channel.forum

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.event.events.channel.ChannelEvent
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler

/**
 * 论坛事件的基类接口。
 *
 * 所有与频道内论坛（话题子频道）相关的事件均直接或间接实现此接口。
 * 论坛是频道内的一种特殊子频道类型，用户在论坛中可以发帖（主题帖）、评论和回复，
 * 形成类似社区论坛的讨论模式。
 *
 * 此接口作为论坛事件体系的顶层抽象，本身不包含业务属性，仅用于统一标识论坛类事件。
 * 具体的论坛事件由子接口和实现类提供，包括：
 * - 主题（Thread）事件：发帖、更新、删除主题
 * - 帖子（Post）事件：创建、删除帖子
 * - 回复（Reply）事件：创建、删除回复/评论
 * - 审核（Audit）事件：帖子发布审核结果
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * @see ForumThreadEvent 论坛主题事件基类
 * @see ForumPostEvent 论坛帖子事件基类
 * @see ForumReplyEvent 论坛回复事件基类
 * @see ForumPublishAuditEvent 论坛发布审核事件
 */
@EventAnnotation.EventMetaType("Not_MetaType_ForumEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
interface ForumEvent : ChannelEvent {
    override val metadataType: String
        get() = "Not_MetaType_LiveRoomEvent"
}
