package io.github.zimoyin.qqbot.event.events.channel

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.event.events.Event


/**
 * 频道事件的基类接口。
 *
 * 所有与频道（频道服务器）相关的事件均直接或间接实现此接口。
 * 该接口定义了频道事件共有的核心属性 {@link #channel}，用于获取事件所属的频道/子频道对象。
 *
 * 作为事件体系中的抽象层，此接口本身不会被实例化，而是由具体的频道事件
 * （如频道成员事件、子频道事件、论坛事件、直播事件等）来实现。
 *
 * @property channel 事件所属的频道或子频道对象，包含频道ID、子频道ID等标识信息
 *
 * @author : zimo
 * @date : 2023/12/08
 *
 * @see io.github.zimoyin.qqbot.event.events.channel.guild.GuildOperate 频道操作事件
 * @see io.github.zimoyin.qqbot.event.events.channel.member.GuildMemberEvent 频道成员事件
 */

@EventAnnotation.EventMetaType("Not_MetaType_ChannelEvent")
@EventAnnotation.EventHandler(ignore = true)
interface ChannelEvent : Event {
    /**
     * 事件所属的频道或子频道对象。
     *
     * 该属性封装了频道的基本信息（频道ID、子频道ID等），
     * 可通过此对象进一步获取频道的详细数据或执行频道相关操作。
     */
    val channel: Channel
}
