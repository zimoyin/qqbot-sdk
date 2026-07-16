package io.github.zimoyin.qqbot.event.events.bot

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.event.events.Event
import io.github.zimoyin.qqbot.event.handler.BotResumedHandler

/**
 * 机器人状态变更事件根接口。
 *
 * 定义机器人与 QQ 服务器之间连接状态变化的基础事件类型。
 * 所有与机器人连接状态相关的事件均实现此接口，形成了一个完整的状态生命周期。
 *
 * **子事件类型及生命周期：**
 *
 * 正常上线流程：
 * 1. {@link io.github.zimoyin.qqbot.event.events.platform.bot.BotHelloEvent} — 连接建立，等待鉴权
 * 2. {@link BotReadyEvent} — 鉴权完成，首次上线（READY）
 *
 * 重连流程：
 * 1. {@link io.github.zimoyin.qqbot.event.events.platform.bot.BotReconnectNotificationEvent} — 服务器要求重连
 * 2. {@link io.github.zimoyin.qqbot.event.events.platform.bot.BotOfflineEvent} — 旧连接断开
 * 3. {@link io.github.zimoyin.qqbot.event.events.platform.bot.BotHelloEvent} — 新连接建立
 * 4. {@link BotResumedEvent} — 会话恢复（RESUMED）
 *
 * 下线：
 * - {@link io.github.zimoyin.qqbot.event.events.platform.bot.BotOfflineEvent} — 连接断开
 *
 * 上线通知：
 * - {@link io.github.zimoyin.qqbot.event.events.platform.bot.BotOnlineEvent} — 上线完成 / WebHook 验证
 *
 * **重要说明：**
 * - 此接口为标记接口（marker interface），定义了机器人状态事件的类型层次。
 * - 部分子事件同时实现了 {@link io.github.zimoyin.qqbot.event.events.platform.PlatformEvent}，
 *   因此它们既可以通过普通事件总线下发，也可以作为平台事件在全局总线中传播。
 *
 * @author : zimo
 * @date : 2023/12/20
 * @see BotReadyEvent
 * @see BotResumedEvent
 * @see io.github.zimoyin.qqbot.event.events.platform.bot.BotHelloEvent
 * @see io.github.zimoyin.qqbot.event.events.platform.bot.BotOnlineEvent
 * @see io.github.zimoyin.qqbot.event.events.platform.bot.BotOfflineEvent
 * @see io.github.zimoyin.qqbot.event.events.platform.bot.BotReconnectNotificationEvent
 */
@EventAnnotation.EventMetaType("Not_MetaType_BotStatusEvent")
@EventAnnotation.EventHandler(BotResumedHandler::class, true)
interface BotStatusEvent : Event
