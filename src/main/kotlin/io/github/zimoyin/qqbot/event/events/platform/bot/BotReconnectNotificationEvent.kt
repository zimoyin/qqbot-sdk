package io.github.zimoyin.qqbot.event.events.platform.bot

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.events.bot.BotStatusEvent
import io.github.zimoyin.qqbot.event.events.platform.PlatformEvent
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler

/**
 * 服务器要求机器人重连通知事件。
 *
 * 当 QQ 服务器主动要求机器人断开当前连接并重新建立 WebSocket 连接时触发。
 * 这是一种服务器端的负载均衡或维护机制，通常发生在以下场景：
 * - 服务器需要进行负载均衡，将机器人迁移到其他节点
 * - 服务器即将进行计划内维护
 * - 服务器检测到连接异常需要客户端重新协商
 *
 * **重要行为说明：**
 * - 收到此事件后，机器人应尽快完成重连操作，否则可能丢失消息。
 * - 重连成功后，会依次触发 {@link BotOfflineEvent}（旧连接断开）、
 *   {@link BotHelloEvent}（新连接建立）和 {@link io.github.zimoyin.qqbot.event.events.bot.BotResumedEvent}
 *   （会话恢复）或 {@link io.github.zimoyin.qqbot.event.events.bot.BotReadyEvent}（全新会话）。
 *
 * @param botInfo 当前事件所属的机器人实例信息
 * @param metadata 事件元数据
 * @param metadataType 事件类型标识符，固定为 "Platform_BotReconnectNotificationEvent"
 * @param eventID 事件唯一标识符
 * @author : zimo
 * @date : 2023/12/20
 * @see BotOfflineEvent
 * @see BotHelloEvent
 * @see io.github.zimoyin.qqbot.event.events.bot.BotResumedEvent
 * @see BotStatusEvent
 */
@EventAnnotation.EventMetaType("Platform_BotReconnectNotificationEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
class BotReconnectNotificationEvent(
    override val botInfo: BotInfo,
    override val metadata: String = "Platform_BotReconnectNotificationEvent",
    override val metadataType: String = "Platform_BotReconnectNotificationEvent",
    override val eventID: String ="",
) : PlatformEvent, BotStatusEvent
