package io.github.zimoyin.qqbot.event.events.message.at

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Group
import io.github.zimoyin.qqbot.bot.contact.User
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.events.group.GroupEvent
import io.github.zimoyin.qqbot.event.handler.message.GroupAtMessageHandler
import java.util.*

/**
 * 群聊 @机器人的消息事件。
 *
 * 当用户在**群聊**中发送消息并 @提及机器人时触发此事件，事件元数据类型为 {@code GROUP_AT_MESSAGE_CREATE}。
 *
 * 此事件同时继承了 {@link GroupEvent} 和 {@link AtMessageEvent}，因此既可以获取群聊相关的信息
 * （群ID、操作者OpenID等），也可以作为 @消息事件进行统一处理。
 *
 * **典型使用场景：** 群聊中用户通过 @机器人 来触发指令或询问，开发者监听此事件后解析消息内容，
 * 并通过 {@link #reply} 或 {@link #quote} 方法进行被动回复。
 *
 * @author : zimo
 * @date : 2023/12/12
 */
@EventAnnotation.EventMetaType("GROUP_AT_MESSAGE_CREATE")
@EventAnnotation.EventHandler(GroupAtMessageHandler::class)
class GroupAtMessageEvent(
    /**
     * 事件元数据类型，固定为 {@code "GROUP_AT_MESSAGE_CREATE"}。
     */
    override val metadataType: String = "GROUP_AT_MESSAGE_CREATE",

    /**
     * 事件元数据原始字符串，包含事件相关的附加信息。
     */
    override val metadata: String,

    /**
     * 消息的唯一标识符。
     */
    override val msgID: String,

    /**
     * 消息来源的群组会话窗口（{@link Group}）。
     */
    override val windows: Group,

    /**
     * 消息内容链，包含消息的完整内容（包括@提及的文本部分）。
     */
    override val messageChain: MessageChain,

    /**
     * 消息发送者（群成员）。
     */
    override val sender: User,

    /**
     * 当前机器人信息。
     */
    override val botInfo: BotInfo,

    /**
     * 群组的唯一标识符。
     */
    override val groupID: String,

    /**
     * 事件发生的时间戳。
     */
    override val timestamp: Date,

    /**
     * 操作成员的 OpenID，用于标识触发此事件的群成员。
     */
    override val opMemberOpenid: String,

    /**
     * 事件唯一标识符，用于跟踪和去重。
     */
    override val eventID: String = "",

    /**
     * 消息序号计数器，初始值为 1，用于被动回复的顺序控制。
     */
    override var msgSeq: Int = 1,
) : GroupEvent, AtMessageEvent
