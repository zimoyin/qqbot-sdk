package io.github.zimoyin.qqbot.event.events.operation

import io.github.zimoyin.qqbot.annotation.EventAnnotation

/**
 * 关闭机器人操作事件标记接口。
 *
 * 用于标识"关闭/停止机器人"相关的操作事件。继承自 {@link BotOperationEvent}，
 * 在事件系统中用于精确匹配和处理机器人的关闭操作。
 *
 * **使用场景：**
 * - 监听机器人被关闭/停止的事件
 * - 在机器人关闭前执行资源清理（如保存状态、关闭连接等）
 * - 配合事件监听系统实现机器人生命周期的钩子操作
 *
 * **设计说明：**
 * 此接口为标记接口（marker interface），具体的关闭操作事件（如
 * {@code CloseGroupBotEvent}、{@code CloseFriendBotEvent} 等）应实现此接口。
 *
 * @author : zimo
 * @date : 2024/12/31
 * @see BotOperationEvent
 * @see OpenBotOperationEvent
 */
@EventAnnotation.EventMetaType("Not_MetaType_CloseBotOperationEvent")
@EventAnnotation.EventHandler(ignore = true)
interface CloseBotOperationEvent {
}
