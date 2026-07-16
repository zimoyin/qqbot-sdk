package io.github.zimoyin.qqbot.event.events.platform.bot

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.events.bot.BotStatusEvent
import io.github.zimoyin.qqbot.event.events.platform.PlatformEvent
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler

/**
 * 机器人 Hello 事件。
 *
 * 当机器人刚刚连接到 QQ 服务器，但**尚未完成鉴权**时触发。
 * 服务器通过下发 Hello 数据包来告知机器人连接已建立，
 * 此时机器人可以准备发送鉴权信息（Identify）来完成完整的登录流程。
 *
 * 此事件同时实现了 {@link PlatformEvent}（平台事件，全局总线传播）和
 * {@link BotStatusEvent}（机器人状态事件）两个接口。
 *
 * **事件流程：**
 * 1. 机器人建立 WebSocket 连接
 * 2. 服务器下发 Hello → 触发 {@code BotHelloEvent}
 * 3. 机器人发送鉴权信息（Identify）
 * 4. 服务器返回 Ready → 触发 {@link io.github.zimoyin.qqbot.event.events.bot.BotReadyEvent}
 *
 * @param botInfo 当前事件所属的机器人实例信息
 * @param metadata 事件元数据
 * @param metadataType 事件类型标识符，固定为 "Platform_BotHelloEvent"
 * @param eventID 事件唯一标识符
 * @author : zimo
 * @date : 2023/12/20
 * @see BotOnlineEvent
 * @see io.github.zimoyin.qqbot.event.events.bot.BotReadyEvent
 * @see BotStatusEvent
 */
@EventAnnotation.EventMetaType("Platform_BotHelloEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
class BotHelloEvent(
    override val botInfo: BotInfo,
    override val metadata: String = "Platform_BotHelloEvent",
    override val metadataType: String = "Platform_BotHelloEvent",
    override val eventID: String ="",
) : PlatformEvent, BotStatusEvent
