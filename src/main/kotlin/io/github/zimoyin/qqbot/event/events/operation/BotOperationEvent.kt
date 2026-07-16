package io.github.zimoyin.qqbot.event.events.operation

import io.github.zimoyin.qqbot.annotation.EventAnnotation

/**
 * 机器人操作事件标记接口。
 *
 * 作为所有机器人操作类事件的顶层标记接口，不包含任何抽象方法或属性。
 * 用于在事件系统中标识和分类所有与机器人操作相关的事件。
 *
 * **子接口类型：**
 * - {@link OpenBotOperationEvent} — 开启机器人操作事件
 * - {@link CloseBotOperationEvent} — 关闭机器人操作事件
 *
 * **设计说明：**
 * 此接口设计为纯标记接口（marker interface），仅用于事件类型体系的层次划分。
 * 具体的操作事件通过实现此接口及其子接口来声明自身所属的事件类别。
 *
 * @author : zimo
 * @date : 2024/12/31
 * @see OpenBotOperationEvent
 * @see CloseBotOperationEvent
 */
@EventAnnotation.EventMetaType("Not_MetaType_BotOperationEvent")
@EventAnnotation.EventHandler(ignore = true)
interface BotOperationEvent {
}
