package io.github.zimoyin.qqbot.event.events.friend

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.friend.DeleteFriendHandler
import java.util.*

/**
 * 用户删除机器人好友事件。
 *
 * 当有用户将机器人从好友列表中删除时，QQ 服务器会推送此事件。
 * 事件类型标识为 `"FRIEND_DEL"`，由 [DeleteFriendHandler] 负责处理。
 *
 * 该事件继承自 [FriendUpdateEvent]，可通过其访问被删除的好友信息。
 * 删除后，机器人无法再向该用户发送私聊消息。
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("FRIEND_DEL")
@EventAnnotation.EventHandler(DeleteFriendHandler::class)
data class DeleteFriendEvent(
    /**
     * 事件的元数据/原始 JSON 数据。
     */
    override val metadata: String,
    /**
     * 事件类型标识符，固定为 `"FRIEND_DEL"`。
     */
    override val metadataType: String = "FRIEND_DEL",
    /**
     * 当前事件所属的机器人实例信息。
     */
    override val botInfo: BotInfo,
    /**
     * 事件发生的时间戳。
     */
    override val timestamp : Date,
    /**
     * 删除机器人的用户好友标识符。
     *
     * 机器人已不再是该用户的好友，后续无法向该用户发送私聊消息。
     */
    override val friendID: String,
    /**
     * 事件的唯一标识符。
     */
    override val eventID: String ="",
): FriendUpdateEvent
