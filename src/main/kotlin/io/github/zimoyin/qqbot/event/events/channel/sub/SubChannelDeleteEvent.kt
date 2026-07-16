package io.github.zimoyin.qqbot.event.events.channel.sub

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.handler.channel.sub.SubChannelDeleteHandler
import io.github.zimoyin.qqbot.net.bean.ChannelBean


/**
 * 子频道删除事件。
 *
 * 当频道服务器中某个子频道被删除时触发此事件，对应的 EventType 为 `CHANNEL_DELETE`。
 * 该事件携带被删除的子频道的完整信息，可通过 [channelBean] 获取被删除子频道的各项属性。
 *
 * 常见触发场景：管理员或具有相应权限的用户在频道内删除某个子频道。
 * 注意：子频道被删除后，其中的所有消息、帖子等数据将无法再访问。
 *
 * @param metadata 事件的原始元数据字符串（JSON 格式），包含事件类型等基础信息
 * @param botInfo 当前接收到该事件的机器人实例信息，包含机器人的身份标识和连接会话
 * @param metadataType 事件元数据类型标识，固定值为 `"CHANNEL_DELETE"`，用于区分不同事件类型
 * @param channelBean 被删除的子频道的频道信息 Bean，包含子频道ID、名称、类型等完整属性
 * @param eventID 事件的唯一标识符，用于去重和追踪，默认为空字符串
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * @see SubChannelEvent 子频道事件基类
 */
@EventAnnotation.EventMetaType("CHANNEL_DELETE")
@EventAnnotation.EventHandler(SubChannelDeleteHandler::class)
data class SubChannelDeleteEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val metadataType: String = "CHANNEL_DELETE",
    override val channelBean: ChannelBean,
    override val eventID: String ="",
) : SubChannelEvent
