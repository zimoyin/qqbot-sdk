package io.github.zimoyin.qqbot.event.events.friend

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.events.operation.CloseBotOperationEvent
import io.github.zimoyin.qqbot.event.handler.friend.CloseFriendBotHandler
import java.util.*

/**
 * C2C 用户关闭机器人消息接收事件。
 *
 * 当用户在 C2C 私聊会话中操作关闭了机器人的消息接收（拒绝机器人接收私聊消息）时，
 * QQ 服务器会推送此事件。事件类型标识为 `"C2C_MSG_REJECT"`，
 * 由 [CloseFriendBotHandler] 负责处理。
 *
 * 该事件同时继承 [CloseBotOperationEvent] 和 [FriendBotOperationEvent]，
 * 表示这是一个关闭类操作，且属于 C2C 私聊与机器人的交互事件。
 * 关闭后，机器人将无法再接收到该用户的私聊消息推送。
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("C2C_MSG_REJECT")
@EventAnnotation.EventHandler(CloseFriendBotHandler::class)
data class CloseFriendBotEvent(
    /**
     * 事件的元数据/原始 JSON 数据。
     */
    override val metadata: String,
    /**
     * 事件类型标识符，固定为 `"C2C_MSG_REJECT"`。
     */
    override val metadataType: String = "C2C_MSG_REJECT",
    /**
     * 当前事件所属的机器人实例信息。
     */
    override val botInfo: BotInfo,
    /**
     * 事件发生的时间戳。
     */
    override val timestamp: Date,
    /**
     * 关闭消息接收的用户好友标识符。
     */
    override val friendID: String,
    /**
     * 事件的唯一标识符。
     */
    override val eventID: String = "",
) : CloseBotOperationEvent, FriendBotOperationEvent
