package io.github.zimoyin.qqbot.event.events.channel.member

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.channel.member.GuildMemberAddHandler
import io.github.zimoyin.qqbot.net.bean.MemberWithGuildID


/**
 * 频道新增成员时触发的事件。
 *
 * 该事件对应 QQ 开放平台的 {@code GUILD_MEMBER_ADD} 事件类型。
 * 当有新用户加入频道时，QQ 后端会主动推送此事件。
 *
 * 事件中的 {@link #member} 属性包含新加入成员的基本信息，
 * 可用于执行新人欢迎、数据统计、自动分配角色等业务逻辑。
 *
 * <p><b>注意：</b>此事件仅在新成员首次加入频道时触发，
 * 不包含成员重新加入（退出后再次加入）的场景。</p>
 *
 * @see GuildMemberEvent 频道成员事件基类
 * @see GuildMemberDeleteEvent 频道成员离开事件
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("GUILD_MEMBER_ADD")
@EventAnnotation.EventHandler(GuildMemberAddHandler::class)
data class GuildMemberAddEvent(
    /**
     * 事件原始元数据，通常为 JSON 字符串，包含事件的基本标识信息。
     */
    override val metadata: String,
    /**
     * 接收此事件的机器人实例信息。
     */
    override val botInfo: BotInfo,
    /**
     * 事件类型标识，固定为 {@code "GUILD_MEMBER_ADD"}。
     */
    override val metadataType: String = "GUILD_MEMBER_ADD",
    /**
     * 新加入频道的成员信息。
     */
    override val member: MemberWithGuildID,
    /**
     * 事件唯一标识符。
     */
    override val eventID: String = "",
) : GuildMemberEvent
