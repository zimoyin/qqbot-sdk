package io.github.zimoyin.qqbot.event.events.channel.member

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.channel.member.GuildMemberUpdateHandler
import io.github.zimoyin.qqbot.net.bean.MemberWithGuildID


/**
 * 频道成员信息发生更新时触发的事件。
 *
 * 该事件对应 QQ 开放平台的 {@code GUILD_MEMBER_UPDATE} 事件类型。
 * 当频道中某个成员的信息（如昵称、角色、权限等）被修改后，
 * QQ 后端会主动推送此事件。
 *
 * <p><b>重要：</b>事件中的 {@link #member} 数据是变更后的完整成员信息，
 * 而非增量变更数据。如果业务需要比对变更前后的差异，应在本地缓存变更前的成员数据。</p>
 *
 * <p><b>常见触发场景：</b></p>
 * <ul>
 *   <li>成员在频道内的昵称被修改；</li>
 *   <li>成员的角色或权限组被调整；</li>
 *   <li>成员被禁言或解除禁言。</li>
 * </ul>
 *
 * @see GuildMemberEvent 频道成员事件基类
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("GUILD_MEMBER_UPDATE")
@EventAnnotation.EventHandler(GuildMemberUpdateHandler::class)
data class GuildMemberUpdateEvent(
    /**
     * 事件原始元数据，通常为 JSON 字符串，包含事件的基本标识信息。
     */
    override val metadata: String,
    /**
     * 接收此事件的机器人实例信息。
     */
    override val botInfo: BotInfo,
    /**
     * 事件类型标识，固定为 {@code "GUILD_MEMBER_UPDATE"}。
     */
    override val metadataType: String = "GUILD_MEMBER_UPDATE",
    /**
     * 更新后的成员完整信息（非增量数据）。
     */
    override val member: MemberWithGuildID,
    /**
     * 事件唯一标识符。
     */
    override val eventID: String = "",
) : GuildMemberEvent
