package io.github.zimoyin.qqbot.event.events.channel.member

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.channel.member.GuildMemberDeleteHandler
import io.github.zimoyin.qqbot.net.bean.MemberWithGuildID


/**
 * 频道成员离开或被移除时触发的事件。
 *
 * 该事件对应 QQ 开放平台的 {@code GUILD_MEMBER_REMOVE} 事件类型。
 * 当频道中的某位成员主动退出或被管理员移除时，QQ 后端会主动推送此事件。
 *
 * <p><b>触发场景包括但不限于：</b></p>
 * <ul>
 *   <li>成员主动退出频道；</li>
 *   <li>成员被管理员踢出频道；</li>
 *   <li>成员因违规被平台封禁。</li>
 * </ul>
 *
 * 事件中的 {@link #member} 包含被移除成员的最后已知信息，
 * 可用于记录离群日志、清理成员相关数据等业务逻辑。
 *
 * @see GuildMemberEvent 频道成员事件基类
 * @see GuildMemberAddEvent 新增频道成员事件
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("GUILD_MEMBER_REMOVE")
@EventAnnotation.EventHandler(GuildMemberDeleteHandler::class)
data class GuildMemberDeleteEvent(
    /**
     * 事件类型标识，固定为 {@code "GUILD_MEMBER_REMOVE"}。
     */
    override val metadataType: String = "GUILD_MEMBER_REMOVE",
    /**
     * 事件原始元数据，通常为 JSON 字符串，包含事件的基本标识信息。
     */
    override val metadata: String,
    /**
     * 接收此事件的机器人实例信息。
     */
    override val botInfo: BotInfo,
    /**
     * 离开或被移除的成员信息（最后已知数据）。
     */
    override val member: MemberWithGuildID,
    /**
     * 事件唯一标识符。
     */
    override val eventID: String = "",
) : GuildMemberEvent
