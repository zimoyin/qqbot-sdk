package io.github.zimoyin.qqbot.event.events.channel.sub

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.channel.sub.SubChannelCreateHandler
import io.github.zimoyin.qqbot.net.bean.ChannelBean


/**
 * 子频道创建事件。
 *
 * 当频道服务器中新建一个子频道时触发此事件，对应的 EventType 为 `CHANNEL_CREATE`。
 * 该事件携带新创建的子频道的完整信息，可通过 [channelBean] 获取子频道的各项属性。
 *
 * 常见触发场景：管理员或具有相应权限的用户在频道内创建新的文字频道、语音频道等。
 *
 * @param metadata 事件的原始元数据字符串（JSON 格式），包含事件类型等基础信息
 * @param botInfo 当前接收到该事件的机器人实例信息，包含机器人的身份标识和连接会话
 * @param metadataType 事件元数据类型标识，固定值为 `"CHANNEL_CREATE"`，用于区分不同事件类型
 * @param channelBean 新创建的子频道的频道信息 Bean，包含子频道ID、名称、类型等完整属性
 * @param eventID 事件的唯一标识符，用于去重和追踪，默认为空字符串
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * @see SubChannelEvent 子频道事件基类
 */
@EventAnnotation.EventMetaType("CHANNEL_CREATE")
@EventAnnotation.EventHandler(SubChannelCreateHandler::class)
data class SubChannelCreateEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val metadataType: String = "CHANNEL_CREATE",
    override val channelBean: ChannelBean,
    override val eventID: String ="",
) : SubChannelEvent
