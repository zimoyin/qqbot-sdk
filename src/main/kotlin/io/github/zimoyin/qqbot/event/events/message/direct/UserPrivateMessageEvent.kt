package io.github.zimoyin.qqbot.event.events.message.direct

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.PrivateFriend
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.events.friend.FriendEvent
import io.github.zimoyin.qqbot.event.handler.message.UserPrivateMessageHandler
import java.util.*

/**
 * C2C（Client to Client）单聊私信消息事件。
 *
 * 当用户通过**好友私聊**（C2C 单聊）向机器人发送消息时触发此事件，事件元数据类型为 {@code C2C_MESSAGE_CREATE}。
 *
 * 此事件同时继承了 {@link FriendEvent} 和 {@link PrivateMessageEvent} 标记接口，
 * 因此既可以获取好友相关信息（好友ID、时间戳等），也可以通过 {@link PrivateMessageEvent}
 * 标记接口与其他私信事件（如 {@link ChannelPrivateMessageEvent}）统一分类处理。
 *
 * **与 {@link ChannelPrivateMessageEvent} 的区别：**
 * - {@code ChannelPrivateMessageEvent} 是频道内的私信，发送者类型为 {@code ChannelPrivateUser}
 * - {@code UserPrivateMessageEvent} 是好友间的C2C单聊，发送者类型为 {@code PrivateFriend}，
 *   且不需要频道上下文
 *
 * **典型使用场景：** 机器人作为QQ好友，接收用户直接发来的私聊消息，进行一对一对话。
 *
 * @author : zimo
 * @date : 2023/12/12
 */
@EventAnnotation.EventMetaType("C2C_MESSAGE_CREATE")
@EventAnnotation.EventHandler(UserPrivateMessageHandler::class)
class UserPrivateMessageEvent(
    /**
     * 事件元数据类型，固定为 {@code "C2C_MESSAGE_CREATE"}。
     */
    override val metadataType: String = "C2C_MESSAGE_CREATE",

    /**
     * 事件元数据原始字符串，包含事件相关的附加信息。
     */
    override val metadata: String,

    /**
     * 消息的唯一标识符。
     */
    override val msgID: String,

    /**
     * 消息来源的私聊会话窗口（{@link PrivateFriend}），代表发送私信的好友。
     */
    override val windows: PrivateFriend,

    /**
     * 消息内容链，包含消息的完整内容。
     */
    override val messageChain: MessageChain,

    /**
     * 消息发送者（私聊好友），与 {@code windows} 为同一对象。
     */
    override val sender: PrivateFriend,

    /**
     * 当前机器人信息。
     */
    override val botInfo: BotInfo,

    /**
     * 好友的唯一标识符。
     */
    override val friendID: String,

    /**
     * 事件发生的时间戳。
     */
    override val timestamp: Date,

    /**
     * 事件唯一标识符，用于跟踪和去重。
     */
    override val eventID: String = "",

    /**
     * 消息序号计数器，初始值为 1，用于被动回复的顺序控制。
     */
    override var msgSeq: Int = 1,
) : FriendEvent, PrivateMessageEvent
