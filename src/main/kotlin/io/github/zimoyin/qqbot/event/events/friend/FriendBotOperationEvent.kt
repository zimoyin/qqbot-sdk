package io.github.zimoyin.qqbot.event.events.friend

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.event.events.operation.BotOperationEvent

/**
 * 私聊管理与机器人操作事件的标记接口。
 *
 * 组合了 [FriendEvent] 和 [BotOperationEvent] 两个接口，用于标识
 * 用户在 C2C 私聊会话中对机器人进行操作（如开启/关闭消息接收）的相关事件。
 *
 * 具体实现类包括 [OpenFriendBotEvent]（开启消息接收）和 [CloseFriendBotEvent]（关闭消息接收）。
 * 该接口本身不定义额外字段，仅用于事件类型层级的分类。
 * 使用了 [EventAnnotation.EventHandler] 注解并设置 `ignore = true`，
 * 不直接参与事件处理器的匹配。
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("Not_MetaType_FriendBotOperationEvent")
@EventAnnotation.EventHandler(ignore = true)
interface FriendBotOperationEvent : FriendEvent,BotOperationEvent
