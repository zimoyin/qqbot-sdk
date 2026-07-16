package io.github.zimoyin.qqbot.event.events.group.operation

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.event.events.group.GroupEvent
import io.github.zimoyin.qqbot.event.events.operation.BotOperationEvent

/**
 * 群管理与机器人操作事件的标记接口。
 *
 * 组合了 [GroupEvent] 和 [BotOperationEvent] 两个接口，用于标识
 * 群管理员在群聊中对机器人进行操作（如开启/关闭消息通知）的相关事件。
 *
 * 具体实现类包括 [OpenGroupBotEvent]（开启通知）和 [CloseGroupBotEvent]（关闭通知）。
 * 该接口本身不定义额外字段，仅用于事件类型层级的分类。
 * 使用了 [EventAnnotation.EventHandler] 注解并设置 `ignore = true`，
 * 不直接参与事件处理器的匹配。
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("Not_MetaType_GroupBotOperationEvent")
@EventAnnotation.EventHandler(ignore = true)
interface GroupBotOperationEvent : GroupEvent, BotOperationEvent
