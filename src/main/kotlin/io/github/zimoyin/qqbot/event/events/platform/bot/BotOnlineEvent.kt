package io.github.zimoyin.qqbot.event.events.platform.bot

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.events.bot.BotStatusEvent
import io.github.zimoyin.qqbot.event.events.platform.PlatformEvent
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler

/**
 * 机器人上线事件 / WebHook 验证服务器事件。
 *
 * 当机器人完成鉴权并成功上线时触发，也可作为 WebHook 模式下服务器的验证事件。
 * 此事件表明机器人已成功连接到 QQ 服务器并通过了身份验证，
 * 可以开始正常接收和发送消息。
 *
 * 此事件同时实现了 {@link PlatformEvent}（平台事件，全局总线传播）和
 * {@link BotStatusEvent}（机器人状态事件）两个接口。
 *
 * **典型使用场景：**
 * - 在机器人上线后执行初始化逻辑（如加载配置、预热缓存）
 * - 记录机器人上线日志
 * - WebHook 服务器地址验证时的回调处理
 *
 * @param metadata 事件元数据，通常包含服务器返回的原始 JSON 数据
 * @param metadataType 事件类型标识符，固定为 "Platform_BotOnlineEvent"
 * @param botInfo 当前事件所属的机器人实例信息
 * @param eventID 事件唯一标识符
 * @author : zimo
 * @date : 2023/12/20
 * @see BotOfflineEvent
 * @see BotStatusEvent
 */
@EventAnnotation.EventMetaType("Platform_BotOnlineEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
class BotOnlineEvent(
    override val metadataType: String = "Platform_BotOnlineEvent",
    override val metadata: String,
    override val botInfo: BotInfo,
    override val eventID: String ="",
) : PlatformEvent, BotStatusEvent
