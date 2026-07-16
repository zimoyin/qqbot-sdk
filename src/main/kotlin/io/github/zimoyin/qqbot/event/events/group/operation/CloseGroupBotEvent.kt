package io.github.zimoyin.qqbot.event.events.group.operation

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.events.operation.CloseBotOperationEvent
import io.github.zimoyin.qqbot.event.handler.group.CloseGroupBotHandler
import java.util.*

/**
 * 群管理员关闭机器人消息通知事件。
 *
 * 当群管理员在机器人资料页操作关闭了消息通知（拒绝机器人接收群消息）时，
 * QQ 服务器会推送此事件。事件类型标识为 `"GROUP_MSG_REJECT"`，
 * 由 [CloseGroupBotHandler] 负责处理。
 *
 * 该事件同时继承 [CloseBotOperationEvent] 和 [GroupBotOperationEvent]，
 * 表示这是一个关闭类操作，且属于群管理与机器人的交互事件。
 * 关闭后，机器人将无法再接收到该群的任何消息推送。
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("GROUP_MSG_REJECT")
@EventAnnotation.EventHandler(CloseGroupBotHandler::class)
data class CloseGroupBotEvent(
    /**
     * 事件的元数据/原始 JSON 数据。
     */
    override val metadata: String,
    /**
     * 事件类型标识符，固定为 `"GROUP_MSG_REJECT"`。
     */
    override val metadataType: String = "GROUP_MSG_REJECT",
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
     * 操作者的成员 OpenID（关闭通知的群管理员）。
     */
    override val opMemberOpenid: String,
    /**
     * 事件的唯一标识符。
     */
    override val eventID: String ="",
) : CloseBotOperationEvent,GroupBotOperationEvent
