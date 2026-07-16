package io.github.zimoyin.qqbot.event.events.bot

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.BotResumedHandler

/**
 * 机器人重连成功 / 会话恢复事件（RESUMED）。
 *
 * 当机器人断线重连后，成功恢复了之前的 WebSocket 会话状态时触发。
 * 这表示重连成功且之前未处理的事件和消息将会继续推送。
 *
 * 与 {@link BotReadyEvent} 的区别：
 * - {@code BotResumedEvent}：重连后成功恢复了之前的会话，不会丢失任何事件
 * - {@link BotReadyEvent}：全新连接或会话恢复失败时的重新上线，之前的事件可能丢失
 *
 * **重连流程：**
 * 1. 服务器发送 {@link io.github.zimoyin.qqbot.event.events.platform.bot.BotReconnectNotificationEvent}
 * 2. 机器人断开连接，触发 {@link io.github.zimoyin.qqbot.event.events.platform.bot.BotOfflineEvent}
 * 3. 机器人重新建立 WebSocket 连接
 * 4. 机器人发送 Resume 请求（携带 {@link BotReadyEvent#sessionID}）
 * 5. 如果会话恢复成功，触发 {@code BotResumedEvent}
 * 6. 如果会话恢复失败（如 sessionID 过期），则触发 {@link BotReadyEvent} 走全新连接流程
 *
 * @param metadata 事件元数据，通常包含服务器返回的完整 JSON 载荷
 * @param metadataType 事件类型标识符，固定为 "RESUMED"
 * @param botInfo 当前事件所属的机器人实例信息
 * @param eventID 事件唯一标识符
 * @author : zimo
 * @date : 2023/12/20
 * @see BotReadyEvent
 * @see BotStatusEvent
 * @see io.github.zimoyin.qqbot.event.events.platform.bot.BotReconnectNotificationEvent
 */
@EventAnnotation.EventMetaType("RESUMED")
@EventAnnotation.EventHandler(BotResumedHandler::class)
data class BotResumedEvent(
    override val metadata: String,
    override val metadataType: String,
    override val botInfo: BotInfo,
    override val eventID: String ="",
) : BotStatusEvent
