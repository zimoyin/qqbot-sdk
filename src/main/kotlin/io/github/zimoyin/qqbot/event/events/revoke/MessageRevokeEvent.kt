package io.github.zimoyin.qqbot.event.events.revoke

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Contact
import io.github.zimoyin.qqbot.bot.contact.User
import io.github.zimoyin.qqbot.event.events.Event
import io.github.zimoyin.qqbot.event.handler.message.MessageHandler

/**
 * 消息撤回事件根接口。
 *
 * 当某条消息被撤回（又称"撤回"或"删除"）时触发的事件。
 * 消息撤回可能由消息发送者、群管理员或系统自动执行。
 *
 * **子事件类型：**
 * - {@link ChannelMessageRevokeEvent} — 频道公开消息被撤回（MESSAGE_DELETE）
 * - {@link ChannelPrivateMessageRevokeEvent} — 频道私信消息被撤回（DIRECT_MESSAGE_DELETE）
 *
 * **重要说明：**
 * - {@code operatorID} 标识实际执行撤回操作的用户，可能与 {@code sender}（原消息发送者）不同。
 *   例如：群管理员撤回他人消息时，{@code sender} 是原消息发送者，{@code operatorID} 是管理员。
 * - {@code windows} 表示消息所在的会话窗口，可用于确定撤回消息的具体上下文（哪个群、哪个频道等）。
 *
 * @author : zimo
 * @date : 2023/12/20
 * @see ChannelMessageRevokeEvent
 * @see ChannelPrivateMessageRevokeEvent
 */
@EventAnnotation.EventMetaType("Not_MetaType_MessageRevokeEvent")
@EventAnnotation.EventHandler(MessageHandler::class, true)
interface MessageRevokeEvent : Event {
    override val metadataType: String
    override val metadata: String
    override val botInfo: BotInfo
    /**
     * 被撤回消息的唯一标识符（消息ID）。
     */
    val msgID: String
    /**
     * 被撤回消息所在的会话窗口/联系人。
     *
     * 用于确定撤回消息的具体上下文，如所属群组、频道或私信会话。
     */
    val windows: Contact
    /**
     * 原消息的发送者。
     *
     * 标识最初发送该消息的用户。注意：此用户不一定与执行撤回操作的用户相同。
     */
    val sender: User

    /**
     * 执行撤回操作的用户ID。
     *
     * 标识实际执行撤回操作的用户。可能与 {@code sender}（原消息发送者）不同，
     * 例如当群管理员撤回其他成员的消息时。
     */
    val operatorID: String
}
