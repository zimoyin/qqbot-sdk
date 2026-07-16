package io.github.zimoyin.qqbot.event.events.channel.guild

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.channel.guild.BotGuildUpdatedHandler
import io.github.zimoyin.qqbot.net.bean.GuildBean


/**
 * 频道信息发生变更时触发的事件。
 *
 * 该事件对应 QQ 开放平台的 {@code GUILD_UPDATE} 事件类型。
 * 当频道的名称、头像、描述等基础信息被修改后，QQ 后端会推送此事件。
 *
 * <p><b>重要：</b>事件中携带的 {@link #guild} 数据是变更后的完整频道信息，
 * 而非增量变更数据。如果业务需要比对变更前后的差异，应在本地缓存变更前的数据。</p>
 *
 * <p><b>常见触发场景：</b></p>
 * <ul>
 *   <li>频道名称被修改；</li>
 *   <li>频道头像被更换；</li>
 *   <li>频道描述信息被更新；</li>
 *   <li>频道所有者变更。</li>
 * </ul>
 *
 * @see GuildOperate 频道操作事件基类
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("GUILD_UPDATE")
@EventAnnotation.EventHandler(BotGuildUpdatedHandler::class)
data class BotGuildUpdatedEvent(
    /**
     * 事件原始元数据，通常为 JSON 字符串，包含事件的基本标识信息。
     */
    override val metadata: String,
    /**
     * 接收此事件的机器人实例信息。
     */
    override val botInfo: BotInfo,
    /**
     * 事件类型标识，固定为 {@code "GUILD_UPDATE"}。
     */
    override val metadataType: String = "GUILD_UPDATE",
    /**
     * 变更后的频道完整信息（非增量数据）。
     */
    override val guild: GuildBean,
    /**
     * 事件唯一标识符。
     */
    override val eventID: String = "",
) : GuildOperate
