package io.github.zimoyin.qqbot.event.events.message.at

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.contact.ChannelPrivateUser
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.events.message.ChannelMessageEvent
import io.github.zimoyin.qqbot.event.handler.message.ChannelAtMessageHandler

/**
 * 文字子频道 @机器人的消息事件。
 *
 * 当用户在**文字子频道**中发送消息并 @提及机器人时触发此事件，事件元数据类型为 {@code AT_MESSAGE_CREATE}。
 *
 * 此事件同时继承了 {@link ChannelMessageEvent} 和 {@link AtMessageEvent}，
 * 因此既拥有频道消息的完整能力（表情反应管理、消息撤回等），也可以作为 @消息事件进行统一分类处理。
 *
 * **与 {@link PrivateChannelMessageEvent} 的区别：**
 * - {@code PrivateChannelMessageEvent} 监听私域子频道的所有消息（无需 @机器人）
 * - {@code ChannelAtMessageEvent} 监听公域子频道中 @机器人的消息
 *
 * **典型使用场景：** 公域频道中，用户通过 @机器人 触发指令，开发者监听此事件后解析消息内容，
 * 并通过 {@link #reply} 或 {@link #quote} 方法进行被动回复。
 *
 * @author : zimo
 * @date : 2023/12/12
 */
@EventAnnotation.EventMetaType("AT_MESSAGE_CREATE")
@EventAnnotation.EventHandler(ChannelAtMessageHandler::class)
class ChannelAtMessageEvent(
    /**
     * 事件元数据原始字符串，包含事件相关的附加信息。
     */
    override val metadata: String,

    /**
     * 事件元数据类型，通常为 {@code "AT_MESSAGE_CREATE"}。
     */
    override val metadataType: String,

    /**
     * 消息的唯一标识符。
     */
    override val msgID: String,

    /**
     * 消息来源的频道会话窗口（{@link Channel}）。
     */
    override val windows: Channel,

    /**
     * 消息内容链，包含消息的完整内容（包括@提及的文本部分）。
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
) : ChannelMessageEvent, AtMessageEvent
