package io.github.zimoyin.qqbot.event.events.channel.live

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.contact.channel.ChannelImpl
import io.github.zimoyin.qqbot.event.handler.channel.live.EnterChannelLiveHandler
import io.github.zimoyin.qqbot.net.bean.UserLive


/**
 * 用户进入音视频/直播子频道事件。
 *
 * 当有用户进入频道内的音视频或直播子频道时触发此事件，对应的 EventType 为 `AUDIO_OR_LIVE_CHANNEL_MEMBER_ENTER`。
 * 该事件携带进入直播子频道的用户信息，可通过 [live] 获取用户的相关属性。
 *
 * 常见触发场景：用户点击加入语音频道、进入直播间观看直播等。
 * [channel] 属性会根据 [live] 中的频道组ID和子频道ID自动构建，方便后续业务操作。
 *
 * @param metadata 事件的原始元数据字符串（JSON 格式），包含事件类型等基础信息
 * @param botInfo 当前接收到该事件的机器人实例信息，包含机器人的身份标识和连接会话
 * @param metadataType 事件元数据类型标识，固定值为 `"AUDIO_OR_LIVE_CHANNEL_MEMBER_ENTER"`，用于区分不同事件类型
 * @param live 进入直播子频道的用户直播信息对象，包含用户ID、频道组ID、子频道ID等标识信息
 * @param eventID 事件的唯一标识符，用于去重和追踪，默认为空字符串
 * @param channel 事件所属的直播子频道对象，根据 [live] 中的频道组ID和子频道ID自动构建
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * @see LiveRoomEvent 音视频/直播房间事件基类
 */
@EventAnnotation.EventMetaType("AUDIO_OR_LIVE_CHANNEL_MEMBER_ENTER")
@EventAnnotation.EventHandler(EnterChannelLiveHandler::class)
data class UserEnteredChannelLiveEvent(
    override val metadata: String,
    override val botInfo: BotInfo,
    override val metadataType: String = "AUDIO_OR_LIVE_CHANNEL_MEMBER_ENTER",
    override val live: UserLive,
    override val eventID: String ="",
    override val channel: Channel = ChannelImpl.convert(botInfo, live.guildId!!, live.channelId, live.channelId),
) : LiveRoomEvent
