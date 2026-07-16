package io.github.zimoyin.qqbot.event.events.channel.forum.reply

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.contact.channel.ChannelImpl
import io.github.zimoyin.qqbot.event.events.channel.forum.ForumEvent
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler
import io.github.zimoyin.qqbot.net.bean.ForumReply

/**
 * 论坛回复（Reply）事件的基类接口。
 *
 * 所有与论坛内回复（Reply，即对帖子的回复/评论）相关的事件均直接或间接实现此接口。
 * 回复是用户在论坛帖子下发布的次级评论内容，区别于帖子（Post），回复是对帖子内容的进一步回应。
 *
 * 该接口定义了论坛回复事件共有的核心属性：
 * - [forum]：从事件中解析出的回复信息对象，包含回复ID、内容、作者、所属帖子等属性
 * - [channel]：根据 [forum] 中的频道组ID和子频道ID自动构建的 [Channel] 对象
 *
 * 作为事件体系中的抽象层，此接口本身不会被实例化，而是由具体的回复事件
 * （如回复创建、回复删除事件）来实现。
 *
 * @property forum 事件关联的论坛回复信息对象，包含回复的完整属性。当用户在话题频道内对帖子进行回复或删除回复时，事件中包含该对象
 * @property channel 事件所属的论坛子频道对象，根据回复信息中的频道组ID和子频道ID自动构建
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * @see ForumReplyCreateEvent 论坛回复创建事件
 * @see ForumReplyDeleteEvent 论坛回复删除事件
 */
@EventAnnotation.EventMetaType("Not_MetaType_ForumPostEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
interface ForumReplyEvent : ForumEvent {
    override val metadataType: String
        get() = "Not_MetaType_ForumPostEvent"

    /**
     * 话题频道内对帖子主题评论或删除时生产事件中包含该对象
     */
    val forum: ForumReply


    override val channel: Channel
        get() = ChannelImpl.convert(botInfo,forum.guildId!!,forum.channelId,forum.channelId)
}
