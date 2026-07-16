package io.github.zimoyin.qqbot.event.events.channel.guild

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.channel.guild.BotLeftGuildHandler
import io.github.zimoyin.qqbot.net.bean.GuildBean


/**
 * 机器人离开某个频道时触发的事件。
 *
 * 该事件对应 QQ 开放平台的 {@code GUILD_DELETE} 事件类型。
 * 当机器人从频道服务器中被移除时，QQ 后端会主动推送此事件。
 *
 * <p><b>触发场景包括但不限于：</b></p>
 * <ul>
 *   <li>频道被管理员或创建者解散；</li>
 *   <li>机器人被管理员踢出频道；</li>
 *   <li>机器人的授权被撤销。</li>
 * </ul>
 *
 * 事件中的 {@link #guild} 包含被移除频道的最后已知数据，
 * 可用于清理该频道相关的本地缓存或执行离群处理逻辑。
 *
 * @see GuildOperate 频道操作事件基类
 * @see BotJoinedGuildEvent 机器人加入频道事件
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("GUILD_DELETE")
@EventAnnotation.EventHandler(BotLeftGuildHandler::class)
data class BotLeftGuildEvent(
    /**
     * 事件原始元数据，通常为 JSON 字符串，包含事件的基本标识信息。
     */
    override val metadata: String,
    /**
     * 接收此事件的机器人实例信息。
     */
    override val botInfo: BotInfo,
    /**
     * 事件类型标识，固定为 {@code "GUILD_DELETE"}。
     */
    override val metadataType: String = "GUILD_DELETE",
    /**
     * 机器人被移除的频道详细信息（最后已知数据）。
     */
    override val guild: GuildBean,
    /**
     * 事件唯一标识符。
     */
    override val eventID: String = "",
) : GuildOperate
