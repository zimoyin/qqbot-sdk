package io.github.zimoyin.qqbot.event.events.operation

import io.github.zimoyin.qqbot.annotation.EventAnnotation

/**
 * 开启机器人操作事件标记接口。
 *
 * 用于标识"开启/启动机器人"相关的操作事件。继承自 {@link BotOperationEvent}，
 * 在事件系统中用于精确匹配和处理机器人的开启操作。
 *
 * **使用场景：**
 * - 监听机器人被开启/启动的事件
 * - 在机器人启动时执行自定义初始化逻辑
 * - 配合事件监听系统实现机器人生命周期的钩子操作
 *
 * **设计说明：**
 * 此接口为标记接口（marker interface），具体的开启操作事件（如
 * {@code OpenGroupBotEvent}、{@code OpenFriendBotEvent} 等）应实现此接口。
 *
 * @author : zimo
 * @date : 2024/12/31
 * @see BotOperationEvent
 * @see CloseBotOperationEvent
 */
@EventAnnotation.EventMetaType("Not_MetaType_OpenBotOperationEvent")
@EventAnnotation.EventHandler(ignore = true)
interface OpenBotOperationEvent {
}
