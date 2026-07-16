package io.github.zimoyin.qqbot.event.events.group.operation

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Contact
import io.github.zimoyin.qqbot.bot.contact.GroupImpl
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.bot.message.MessageChainBuilder
import io.github.zimoyin.qqbot.bot.message.type.MessageItem
import io.github.zimoyin.qqbot.event.events.operation.OpenBotOperationEvent
import io.github.zimoyin.qqbot.net.bean.SendMessageResultBean
import io.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future
import java.util.*

/**
 * 群管理员开启机器人消息通知事件。
 *
 * 当群管理员在机器人资料页操作开启了消息通知（允许机器人接收群消息）时，
 * QQ 服务器会推送此事件。事件类型标识为 `"GROUP_MSG_RECEIVE"`，
 * 由 [OpenGroupBotHandler] 负责处理。
 *
 * 该事件同时继承 [OpenBotOperationEvent] 和 [GroupBotOperationEvent]，
 * 表示这是一个开启类操作，且属于群管理与机器人的交互事件。
 * 提供了多个 [reply] 重载方法用于被动回复消息。
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("GROUP_MSG_RECEIVE")
@EventAnnotation.EventHandler(io.github.zimoyin.qqbot.event.handler.group.OpenGroupBotHandler::class)
data class OpenGroupBotEvent(
    /**
     * 事件的元数据/原始 JSON 数据。
     */
    override val metadata: String,
    /**
     * 事件类型标识符，固定为 `"GROUP_MSG_RECEIVE"`。
     */
    override val metadataType: String = "GROUP_MSG_RECEIVE",
    /**
     * 当前事件所属的机器人实例信息。
     */
    override val botInfo: BotInfo,
    /**
     * 群聊的唯一标识符（群号）。
     */
    override val groupID: String,
    /**
     * 事件发生的时间戳。
     */
    override val timestamp: Date,
    /**
     * 操作者的成员 OpenID（开启通知的群管理员）。
     */
    override val opMemberOpenid: String,
    /**
     * 群聊实例对象，用于向该群发送消息。
     */
    val windows: GroupImpl,
    /**
     * 事件的唯一标识符，由 QQ 服务器下发。
     */
    override val eventID: String
) : OpenBotOperationEvent,GroupBotOperationEvent{
    /**
     * 消息序列号，每次调用 [reply] 后自增。
     *
     * 用于保证消息的发送顺序，避免并发场景下的乱序问题。
     */
    var msgSeq: Int = 1

    /**
     * 被动回复文本消息到该群聊。
     *
     * @param msg 要回复的文本内容
     * @return 包含发送结果的 [Future]
     */
    fun reply(msg: String): Future<SendMessageResultBean> {
        return reply(MessageChainBuilder().appendMeqSeq(msgSeq++).append(msg).build())
    }

    /**
     * 被动回复消息链到该群聊。
     *
     * 如果 [message] 中已设置了 [replyEventID]，则优先使用消息中指定的回复 ID。
     * 当 [eventID] 和消息中的 [replyEventID] 均为空时，发送会失败。
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
     * 被动回复一组消息项到该群聊。
     *
     * @param items 要回复的消息项（可变参数）
     * @return 包含发送结果的 [Future]
     */
    fun reply(vararg items: MessageItem): Future<SendMessageResultBean> {
        return reply(MessageChainBuilder().appendMeqSeq(msgSeq++).appendEventId(eventID).appendItems(*items).build())
    }
}
