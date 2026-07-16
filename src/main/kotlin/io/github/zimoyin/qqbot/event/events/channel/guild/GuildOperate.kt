package io.github.zimoyin.qqbot.event.events.channel.guild

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.contact.channel.ChannelImpl
import io.github.zimoyin.qqbot.event.events.channel.ChannelEvent
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler
import io.github.zimoyin.qqbot.net.bean.GuildBean


/**
 * 频道（频道服务器）操作事件的基类接口。
 *
 * 当机器人所在的频道发生整体性变更（如加入、退出、信息更新）时，触发此接口的子类事件。
 * 该接口继承自 {@link ChannelEvent}，并额外提供了 {@link #guild} 属性来获取频道的完整数据 Bean。
 *
 * 注意：此接口本身为抽象层，不直接对应任何 QQ 开放平台的事件类型，
 * 实际触发的是其子类：{@link BotJoinedGuildEvent}、{@link BotLeftGuildEvent}、
 * {@link BotGuildUpdatedEvent}。
 *
 * @property guild 频道的详细信息 Bean，包含频道ID、名称、所有者等完整数据
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * @see BotJoinedGuildEvent 机器人加入频道事件
 * @see BotLeftGuildEvent 机器人离开频道事件
 * @see BotGuildUpdatedEvent 频道信息更新事件
 */
@EventAnnotation.EventMetaType("Not_MetaType_GuildOperate")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
interface GuildOperate : ChannelEvent {
    override val metadataType: String
        get() = "Not_MetaType_GuildOperate"

    /**
     * 频道的详细信息 Bean。
     *
     * 包含频道的ID、名称、所有者、成员数等完整数据。
     * 该 Bean 由 QQ 开放平台的 WebSocket 推送数据反序列化而来。
     */
    val guild: GuildBean

    /**
     * 事件所属的频道对象。
     *
     * 此属性由 {@link #guild} 中的频道ID自动构造，实际上指向的是频道本身而非子频道。
     *
     * @return 基于频道ID构造的 {@link Channel} 实例
     */
    override val channel: Channel
        get() = ChannelImpl.convert(botInfo,guild.id!!,null,guild.id)
}
