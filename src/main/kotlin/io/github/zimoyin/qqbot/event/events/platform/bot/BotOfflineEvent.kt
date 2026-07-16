package io.github.zimoyin.qqbot.event.events.platform.bot

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.events.bot.BotStatusEvent
import io.github.zimoyin.qqbot.event.events.platform.PlatformEvent
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler

/**
 * 机器人下线事件。
 *
 * 当机器人从 QQ 服务器断开连接或主动下线时触发。可能的原因包括：
 * - 网络连接异常断开
 * - 服务器主动断开连接
 * - 鉴权信息失效（token 过期等）
 * - 服务端故障或维护
 *
 * 此事件同时实现了 {@link PlatformEvent}（平台事件，全局总线传播）和
 * {@link BotStatusEvent}（机器人状态事件）两个接口。
 *
 * **典型使用场景：**
 * - 记录下线日志并进行故障排查
 * - 触发自动重连逻辑（配合 {@link BotReconnectNotificationEvent}）
 * - 发送下线告警通知
 *
 * @param botInfo 当前事件所属的机器人实例信息
 * @param throwable 导致下线的异常信息，如果下线是正常操作则为 {@code null}。
 *                  通过此参数可以获取下线原因，用于日志记录和故障排查
 * @param metadata 事件元数据
 * @param metadataType 事件类型标识符，固定为 "Platform_BotOfflineEvent"
 * @param eventID 事件唯一标识符
 * @author : zimo
 * @date : 2023/12/20
 * @see BotOnlineEvent
 * @see BotReconnectNotificationEvent
 * @see BotStatusEvent
 */
@EventAnnotation.EventMetaType("Platform_BotOfflineEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
class BotOfflineEvent(
    override val botInfo: BotInfo,
    /**
     * 导致下线的异常信息。
     *
     * 如果下线是由于异常导致的（如网络断开、鉴权失败等），此字段包含对应的异常对象。
     * 如果下线是正常操作，则为 {@code null}。
     */
    val throwable: Throwable?,
    override val metadata: String = "Platform_BotOfflineEvent",
    override val metadataType: String = "Platform_BotOfflineEvent",
    override val eventID: String ="",
) : PlatformEvent, BotStatusEvent
