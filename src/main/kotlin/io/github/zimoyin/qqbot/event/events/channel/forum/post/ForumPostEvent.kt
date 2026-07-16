package io.github.zimoyin.qqbot.event.events.channel.forum.post

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.contact.channel.ChannelImpl
import io.github.zimoyin.qqbot.event.events.channel.forum.ForumEvent
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler
import io.github.zimoyin.qqbot.net.bean.ForumPost

/**
 * 论坛帖子（Post）事件的基类接口。
 *
 * 所有与论坛内帖子（Post，即对主题帖的评论和回复）相关的事件均直接或间接实现此接口。
 * 帖子是用户在论坛主题帖下发布的评论内容，区别于主题帖（Thread），帖子属于主题的二级内容。
 *
 * 该接口定义了论坛帖子事件共有的核心属性：
 * - [forum]：从事件中解析出的帖子信息对象，包含帖子ID、内容、作者、所属主题等属性
 * - [channel]：根据 [forum] 中的频道组ID和子频道ID自动构建的 [Channel] 对象
 *
 * 作为事件体系中的抽象层，此接口本身不会被实例化，而是由具体的帖子事件
 * （如帖子创建、帖子删除事件）来实现。
 *
 * @property forum 事件关联的论坛帖子信息对象，包含帖子的完整属性。当用户在话题频道内对主题帖进行评论或删除时，事件中包含该对象
 * @property channel 事件所属的论坛子频道对象，根据帖子信息中的频道组ID和子频道ID自动构建
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * @see ForumPostCreateEvent 论坛帖子创建事件
 * @see ForumPostDeleteEvent 论坛帖子删除事件
 */
@EventAnnotation.EventMetaType("Not_MetaType_ForumPostEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
interface ForumPostEvent : ForumEvent {
    override val metadataType: String
        get() = "Not_MetaType_ForumPostEvent"

    /**
     * 话题频道内对帖子主题评论或删除时生产事件中包含该对象
     */
    val forum: ForumPost

    override val channel: Channel
        get() = ChannelImpl.convert(botInfo,forum.guildId!!,forum.channelId,forum.channelId)
}
