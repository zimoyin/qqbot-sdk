package io.github.zimoyin.qqbot.event.events.channel.forum.thread

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.contact.channel.ChannelImpl
import io.github.zimoyin.qqbot.event.events.channel.forum.ForumEvent
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler
import io.github.zimoyin.qqbot.net.bean.ForumThread


/**
 * 论坛主题（Thread）事件的基类接口。
 *
 * 所有与论坛内主题帖（Thread）相关的事件均直接或间接实现此接口。
 * 主题帖是论坛内用户发布的独立话题帖子，每个主题帖可以有多个评论和回复。
 *
 * 该接口定义了论坛主题事件共有的核心属性：
 * - [forum]：从事件中解析出的论坛主题信息对象，包含主题ID、标题、内容、作者等属性
 * - [channel]：根据 [forum] 中的频道组ID和子频道ID自动构建的 [Channel] 对象
 *
 * 作为事件体系中的抽象层，此接口本身不会被实例化，而是由具体的主题事件
 * （如主题创建、更新、删除事件）来实现。
 *
 * @property forum 事件关联的论坛主题信息对象，包含主题帖的完整属性
 * @property channel 事件所属的论坛子频道对象，根据主题信息中的频道组ID和子频道ID自动构建
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * @see ForumThreadCreateEvent 论坛主题创建事件
 * @see ForumThreadUpdateEvent 论坛主题更新事件
 * @see ForumThreadDeleteEvent 论坛主题删除事件
 */
@EventAnnotation.EventMetaType("Not_MetaType_ForumThreadEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
interface ForumThreadEvent : ForumEvent {
    override val metadataType: String
        get() = "Not_MetaType_ForumThreadEvent"

    val forum: ForumThread
    override val channel: Channel
        get() = ChannelImpl.convert(botInfo,forum.guildId!!,forum.channelId,forum.channelId)
}
