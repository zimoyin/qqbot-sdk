package io.github.zimoyin.qqbot.event.events.friend

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.PrivateFriend
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.bot.message.MessageChainBuilder
import io.github.zimoyin.qqbot.bot.message.type.MessageItem
import io.github.zimoyin.qqbot.event.handler.friend.AddFriendHandler
import io.github.zimoyin.qqbot.net.bean.SendMessageResultBean
import io.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future
import java.util.*

/**
 * 用户添加机器人为好友事件。
 *
 * 当有用户添加机器人为好友（将机器人添加为 C2C 私聊好友）时，
 * QQ 服务器会推送此事件。事件类型标识为 `"FRIEND_ADD"`，
 * 由 [AddFriendHandler] 负责处理。
 *
 * 该事件继承自 [FriendUpdateEvent]，可通过其访问好友信息。
 * 提供了多个 [reply] 重载方法，用于被动回复消息（需要有效的 [eventID]）。
 * 通过 [windows] 属性可以获取私聊实例，用于向该用户发送私聊消息。
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("FRIEND_ADD")
@EventAnnotation.EventHandler(AddFriendHandler::class)
data class AddFriendEvent(
    /**
     * 事件的元数据/原始 JSON 数据。
     */
    override val metadata: String,
    /**
     * 事件类型标识符，固定为 `"FRIEND_ADD"`。
     */
    override val metadataType: String = "FRIEND_ADD",
    /**
     * 当前事件所属的机器人实例信息。
     */
    override val botInfo: BotInfo,
    /**
     * 事件发生的时间戳。
     */
    override val timestamp: Date,
    /**
     * 添加机器人的用户好友标识符。
     */
    override val friendID: String,
    /**
     * 事件的唯一标识符。
     *
     * 由 QQ 服务器下发，用于被动回复的去重和关联。为空时表示没有关联的被动回复 ID。
     */
    override val eventID: String = "",
    /**
     * 私聊好友实例对象，用于向该用户发送私聊消息。
     *
     * 通过该实例可以直接调用 `send()` 方法向该好友发送消息。
     */
    val windows: PrivateFriend,
) : FriendUpdateEvent {
    /**
     * 消息序列号，每次调用 [reply] 后自增。
     *
     * 用于保证消息的发送顺序，避免并发场景下的乱序问题。
     */
    var msgSeq: Int = 1

    /**
     * 被动回复文本消息给该好友。
     *
     * 构建一条包含文本消息的 [MessageChain]，然后通过 [windows] 发送到私聊会话。
     * 消息中包含递增的序列号和事件 ID 用于被动回复关联。
     *
     * @param msg 要回复的文本内容
     * @return 包含发送结果的 [Future]，其中 [SendMessageResultBean] 包含消息 ID 等信息
     */
    fun reply(msg: String): Future<SendMessageResultBean> {
        return reply(MessageChainBuilder().appendMeqSeq(msgSeq++).append(msg).build())
    }

    /**
     * 被动回复消息链给该好友。
     *
     * 将指定的 [MessageChain] 消息发送到私聊会话。消息会附带递增的序列号和事件 ID。
     * 如果 [message] 中已设置了 [replyEventID]，则优先使用消息中指定的回复 ID 而非事件自带的 [eventID]。
     *
     * 注意：当 [eventID] 和消息中的 [replyEventID] 均为空时，发送会失败并返回失败的 Future。
     *
     * @param message 要回复的消息链
     * @return 包含发送结果的 [Future]
     */
    fun reply(message: MessageChain): Future<SendMessageResultBean> {
        val eventID = if (message.replyEventID.isNullOrEmpty()) {
            eventID
        } else {
            message.replyEventID
        }
        if (eventID.isEmpty()) return promise<SendMessageResultBean>().apply {
            tryFail("eventID is null")
        }.future()
        return windows.send(
            MessageChainBuilder().appendMeqSeq(msgSeq++).appendEventId(eventID).append(message).build()
        )
    }

    /**
     * 被动回复一组消息项给该好友。
     *
     * 将可变参数传入的 [MessageItem] 组装为消息链，通过 [windows] 发送到私聊会话。
     * 消息包含递增的序列号和当前事件的 [eventID]。
     *
     * @param items 要回复的消息项（可变参数）
     * @return 包含发送结果的 [Future]
     */
    fun reply(vararg items: MessageItem): Future<SendMessageResultBean> {
        return reply(MessageChainBuilder().appendMeqSeq(msgSeq++).appendEventId(eventID).appendItems(*items).build())
    }
}
