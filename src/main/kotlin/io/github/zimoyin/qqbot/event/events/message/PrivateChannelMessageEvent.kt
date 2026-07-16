package io.github.zimoyin.qqbot.event.events.message

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.contact.ChannelPrivateUser
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.handler.message.PrivateChannelMessageHandler

/**
 * 私域文字子频道消息事件。
 *
 * 当用户在**私域**文字子频道中发送聊天消息时触发此事件，事件元数据类型为 {@code MESSAGE_CREATE}。
 *
 * **重要说明：**
 * - 此事件**仅能监听私有域（私域）**的文字子频道消息，不适用于公域频道。
 * - 公域频道的消息需要通过其他事件类型（如 {@link ChannelAtMessageEvent}）来监听。
 *
 * 此事件继承自 {@link ChannelMessageEvent} 和 {@link MessageEvent}，
 * 拥有频道消息的所有能力，包括表情反应管理和消息撤回等。
 *
 * @author : zimo
 * @date : 2023/12/12
 */
@EventAnnotation.EventMetaType("MESSAGE_CREATE")
@EventAnnotation.EventHandler(PrivateChannelMessageHandler::class)
class PrivateChannelMessageEvent(
    /**
     * 事件元数据类型，固定为 {@code "MESSAGE_CREATE"}。
     */
    override val metadataType: String = "MESSAGE_CREATE",

    /**
     * 事件元数据原始字符串，包含事件相关的附加信息。
     */
    override val metadata: String,

    /**
     * 消息的唯一标识符。
     */
    override val msgID: String,

    /**
     * 消息来源的频道会话窗口。
     */
    override val windows: Channel,

    /**
     * 消息内容链，包含消息的完整内容。
     */
    override val messageChain: MessageChain,

    /**
     * 消息发送者（频道私密用户）。
     */
    override val sender: ChannelPrivateUser,

    /**
     * 当前机器人信息。
     */
    override val botInfo: BotInfo,

    /**
     * 关联的频道对象，默认与 {@code windows} 相同。
     */
    override val channel: Channel = windows,

    /**
     * 事件唯一标识符，用于跟踪和去重。
     */
    override val eventID: String = "",

    /**
     * 消息序号计数器，初始值为 1，用于被动回复的顺序控制。
     */
    override var msgSeq: Int = 1,
) : ChannelMessageEvent, MessageEvent
