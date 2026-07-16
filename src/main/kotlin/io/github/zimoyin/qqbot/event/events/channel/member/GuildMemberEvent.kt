package io.github.zimoyin.qqbot.event.events.channel.member

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.contact.channel.ChannelImpl
import io.github.zimoyin.qqbot.event.events.channel.ChannelEvent
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler
import io.github.zimoyin.qqbot.net.bean.MemberWithGuildID


/**
 * 频道成员事件的基类接口。
 *
 * 当频道中的成员发生变动（加入、信息更新、离开）时，触发此接口的子类事件。
 * 该接口继承自 {@link ChannelEvent}，并额外提供了 {@link #member} 属性来获取
 * 触发事件的成员数据。
 *
 * 注意：此接口本身为抽象层，不直接对应任何 QQ 开放平台的事件类型，
 * 实际触发的是其子类：{@link GuildMemberAddEvent}、{@link GuildMemberUpdateEvent}、
 * {@link GuildMemberDeleteEvent}。
 *
 * {@link #channel} 属性根据 {@link #member} 中的频道ID自动推导构造，
 * 代表该成员所属的频道（非子频道）。
 *
 * @property member 触发事件的成员信息 Bean，包含成员ID、昵称、角色等数据
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * @see GuildMemberAddEvent 新增频道成员事件
 * @see GuildMemberUpdateEvent 频道成员更新事件
 * @see GuildMemberDeleteEvent 频道成员离开事件
 */
@EventAnnotation.EventMetaType("Not_MetaType_ChannelMemberEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
interface GuildMemberEvent : ChannelEvent {
    override val metadataType: String
        get() = "Not_MetaType_ChannelMemberEvent"

    /**
     * 触发事件的成员信息 Bean。
     *
     * 包含成员的用户ID、成员昵称、角色权限、加入时间等信息，
     * 以及该成员所属的频道ID（{@code guildID}）。
     */
    val member: MemberWithGuildID

    /**
     * 事件所属的频道对象。
     *
     * 此属性由 {@link #member} 中的频道ID自动构造，指向成员所属的频道（非子频道）。
     *
     * @return 基于成员所属频道ID构造的 {@link Channel} 实例
     */
    override val channel: Channel
        get() = ChannelImpl.convert(botInfo,member.guildID!!,null,member.guildID)
}
