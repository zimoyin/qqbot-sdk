package io.github.zimoyin.qqbot.event.events.group.member

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.group.ExitGroupHandler
import java.util.*

/**
 * 机器人被移出群聊事件。
 *
 * 当机器人被群主或群管理员移出群聊（踢出），或主动退出群聊时，
 * QQ 服务器会推送此事件。事件类型标识为 `"GROUP_DEL_ROBOT"`，
 * 由 [ExitGroupHandler] 负责处理。
 *
 * 该事件继承自 [GroupMemberUpdateEvent]，可通过其访问群聊信息。
 * 移出群聊后，机器人无法再向该群发送消息。
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("GROUP_DEL_ROBOT")
@EventAnnotation.EventHandler(ExitGroupHandler::class)
data class ExitGroupEvent(
    /**
     * 事件的元数据/原始 JSON 数据。
     */
    override val metadata: String,
    /**
     * 事件类型标识符，固定为 `"GROUP_DEL_ROBOT"`。
     */
    override val metadataType: String = "GROUP_DEL_ROBOT",
    /**
     * 当前事件所属的机器人实例信息。
     */
    override val botInfo: BotInfo,
    /**
     * 群聊的唯一标识符（群号）。
     *
     * 机器人已退出该群，后续无法向该群发送消息。
     */
    override val groupID :String,
    /**
     * 事件发生的时间戳。
     */
    override val timestamp :Date,
    /**
     * 操作者的成员 OpenID（将机器人踢出群的管理员或群主）。
     *
     * 如果机器人是主动退出，该值可能为 `"none"`。
     */
    override val opMemberOpenid :String,
    /**
     * 事件的唯一标识符。
     */
    override val eventID: String ="",
): GroupMemberUpdateEvent
