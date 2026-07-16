package io.github.zimoyin.qqbot.event.events.message

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.contact.Contact
import io.github.zimoyin.qqbot.bot.contact.User
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.bot.message.MessageChainBuilder
import io.github.zimoyin.qqbot.bot.message.type.MessageItem
import io.github.zimoyin.qqbot.bot.message.type.ReferenceMessage
import io.github.zimoyin.qqbot.event.events.Event
import io.github.zimoyin.qqbot.event.handler.message.MessageHandler
import io.github.zimoyin.qqbot.net.bean.SendMessageResultBean
import io.vertx.core.Future

/**
 * 消息事件基类接口，是所有消息事件的公共父接口。
 *
 * 提供了消息的基本属性（消息ID、会话窗口、消息链、发送者、消息序号）以及
 * 被动回复（{@link #reply}）和被动引用回复（{@link #quote}）方法。
 *
 * **重要行为说明：**
 * - 通过事件只能进行**被动回复**（即针对当前收到的消息进行回复），无法发送主动消息。
 *   如需主动推送消息，请使用 {@code Content.send} 系列方法。
 * - 每次调用 reply/quote 方法都会自动递增消息序号 {@link #msgSeq}，确保回复的顺序正确性。
 *
 * @author : zimo
 * @date : 2023/12/06/18:25
 */
@EventAnnotation.EventMetaType("Not_MetaType_MessageEvent")
@EventAnnotation.EventHandler(MessageHandler::class, true)
interface MessageEvent : Event {
    /**
     * 消息的唯一标识符（消息ID）。
     *
     * 每条消息在系统中都有一个唯一的ID，可用于引用回复、撤回等操作。
     */
    val msgID: String

    /**
     * 会话窗口 / 消息来源。
     *
     * 表示消息来自于哪个会话上下文，可能是群组（{@code Group}）、频道（{@code Channel}）、
     * 私信好友（{@code PrivateFriend}）等。通过此对象可以调用发送消息等会话相关操作。
     */
    val windows: Contact

    /**
     * 消息内容链。
     *
     * 消息的完整内容表示，由一组 {@link MessageItem} 组成，支持文本、图片、表情、
     * 引用等多种消息类型的组合。可通过 {@link MessageChain} 的流式方法遍历和操作消息内容。
     */
    val messageChain: MessageChain

    /**
     * 消息发送者。
     *
     * 表示发送此消息的用户。具体类型取决于消息来源：群消息为 {@code User}，
     * 频道消息为 {@code ChannelPrivateUser}，私聊消息为 {@code PrivateFriend} 等。
     */
    val sender: User

    /**
     * 消息序号计数器，用于保证被动回复的顺序性。
     *
     * 每次调用被动回复方法（如 {@link #reply}、{@link #quote}）时，该计数器会自动递增，
     * 并将序号附加到回复消息的 {@code msgSeq} 字段中，以确保接收方能按正确顺序处理多条回复。
     * 初始值通常由事件构造时传入，开发者一般无需手动修改此值。
     */
    var msgSeq: Int

    /**
     * 被动回复消息（文本形式）。
     *
     * 向当前消息所在的会话窗口发送一条文本回复。
     *
     * **注意：** 此方法只能发送被动消息，无法发送主动消息。如需主动推送，请使用 {@code Content.send} 系列方法。
     *
     * @param message 要回复的文本内容
     * @return 异步结果，包含发送消息的响应信息（{@link SendMessageResultBean}）
     */
    fun reply(message: String): Future<SendMessageResultBean> {
        return windows.send(MessageChainBuilder(msgID).appendMeqSeq(msgSeq++).append(message).build())
    }

    /**
     * 被动回复消息（多条消息项形式）。
     *
     * 向当前消息所在的会话窗口发送由多个 {@link MessageItem} 组成的回复。
     *
     * **注意：** 此方法只能发送被动消息。对于非文本形式的消息项（如图片、文件等），
     * 可能会受限于主动消息推送的能力限制，请根据实际使用场景测试。
     *
     * @param message 要回复的消息项（可变参数），支持文本、图片、表情等多种类型
     * @return 异步结果，包含发送消息的响应信息（{@link SendMessageResultBean}）
     */
    fun reply(vararg message: MessageItem): Future<SendMessageResultBean> {
        return reply(
            MessageChainBuilder(msgID).appendMeqSeq(msgSeq++).appendItems(*message).build()
        )
    }

    /**
     * 被动回复消息（消息链形式）。
     *
     * 向当前消息所在的会话窗口发送一个完整的 {@link MessageChain} 作为回复。
     * 如果传入的消息链没有指定ID，则会自动使用当前消息的ID；同时会保留消息链中
     * 原有的回复事件ID（{@code replyEventID}），确保回复链路的完整性。
     *
     * **注意：** 此方法只能发送被动消息。对于非文本形式的消息，可能会受限于主动消息推送的能力限制。
     *
     * @param message 要回复的消息链
     * @return 异步结果，包含发送消息的响应信息（{@link SendMessageResultBean}）
     */
    fun reply(message: MessageChain): Future<SendMessageResultBean> {
        return windows.send(
            MessageChainBuilder(message.id ?: msgID).appendMeqSeq(msgSeq++).appendEventId(message.replyEventID)
                .append(message).build()
        )
    }

    /**
     * 被动引用回复消息（文本形式）。
     *
     * 引用当前消息并回复一段文本。与 {@link #reply} 的区别在于，此方法会在回复中
     * 附加对原消息的引用（{@link ReferenceMessage}），使接收方可以清晰地看到回复的上下文。
     *
     * **注意：** 此方法只能发送被动消息，无法发送主动消息。如需主动推送，请使用 {@code Content.send} 系列方法。
     *
     * @param message 要回复的文本内容
     * @return 异步结果，包含发送消息的响应信息（{@link SendMessageResultBean}）
     */
    fun quote(message: String): Future<SendMessageResultBean> {
        return windows.send(
            MessageChainBuilder(msgID).appendMeqSeq(msgSeq++).append(ReferenceMessage(msgID)).append(message).build()
        )
    }

    /**
     * 被动引用回复消息（消息链形式）。
     *
     * 引用当前消息并回复一个完整的 {@link MessageChain}。如果传入的消息链中已经包含
     * {@link ReferenceMessage}，则不再重复添加引用，直接发送原消息链；否则会自动附加对当前消息的引用。
     *
     * **注意：** 此方法只能发送被动消息，无法发送主动消息。如需主动推送，请使用 {@code Content.send} 系列方法。
     *
     * @param message 要回复的消息链
     * @return 异步结果，包含发送消息的响应信息（{@link SendMessageResultBean}）
     */
    fun quote(message: MessageChain): Future<SendMessageResultBean> {
        return if (message.stream().filter { it is ReferenceMessage }.count() == 0.toLong()) {
            windows.send(
                MessageChainBuilder(message.id ?: msgID)
                    .appendMeqSeq(msgSeq++)
                    .reference(msgID)
                    .appendEventId(message.replyEventID)
                    .append(message)
                    .build()
            )
        } else {
            windows.send(
                MessageChainBuilder(message.id ?: msgID)
                    .appendMeqSeq(msgSeq++)
                    .appendEventId(message.replyEventID)
                    .append(message)
                    .build()
            )
        }
    }

    /**
     * 被动回复消息（多条消息项形式，等同于 {@link #reply(vararg MessageItem)}）。
     *
     * 此方法是 {@code reply(vararg message: MessageItem)} 的别名，功能完全一致。
     *
     * @param message 要回复的消息项（可变参数）
     * @return 异步结果，包含发送消息的响应信息（{@link SendMessageResultBean}）
     */
    fun qute(vararg message: MessageItem): Future<SendMessageResultBean> {
        return reply(MessageChainBuilder(msgID).appendMeqSeq(msgSeq++).appendItems(*message).build())
    }
}
