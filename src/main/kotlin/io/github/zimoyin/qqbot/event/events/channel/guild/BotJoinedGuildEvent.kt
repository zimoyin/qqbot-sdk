package io.github.zimoyin.qqbot.event.events.channel.guild

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.channel.guild.BotJoinedGuildHandler
import io.github.zimoyin.qqbot.net.bean.GuildBean


/**
 * 机器人被加入到某个频道时触发的事件。
 *
 * 该事件对应 QQ 开放平台的 {@code GUILD_CREATE} 事件类型。
 * 当机器人被管理员或其他成员邀请加入一个新的频道服务器时，
 * QQ 后端会主动推送此事件。
 *
 * 事件中携带的 {@link #guild} 属性包含该频道的完整信息，
 * 可用于初始化频道级别的数据缓存或执行入群欢迎等逻辑。
 *
 * <p><b>注意：</b>此事件仅在机器人被添加到的那个频道上下文中触发，
 * 其他频道的监听器不会收到此事件。</p>
 *
 * @see GuildOperate 频道操作事件基类
 * @see BotLeftGuildEvent 机器人离开频道事件
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("GUILD_CREATE")
@EventAnnotation.EventHandler(BotJoinedGuildHandler::class)
data class BotJoinedGuildEvent(
    /**
     * 事件原始元数据，通常为 JSON 字符串，包含事件的基本标识信息。
     */
    override val metadata: String,
    /**
     * 接收此事件的机器人实例信息。
     */
    override val botInfo: BotInfo,
    /**
     * 事件类型标识，固定为 {@code "GUILD_CREATE"}。
     */
    override val metadataType: String = "GUILD_CREATE",
    /**
     * 机器人新加入的频道详细信息。
     */
    override val guild: GuildBean,
    /**
     * 事件唯一标识符。
     */
    override val eventID: String = "",
) : GuildOperate
