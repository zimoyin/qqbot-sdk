package io.github.zimoyin.qqbot.event.events.channel.live

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.event.events.channel.ChannelEvent
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler
import io.github.zimoyin.qqbot.net.bean.UserLive


/**
 * 音视频/直播房间事件的基类接口。
 *
 * 所有与频道内音视频/直播子频道相关的事件均直接或间接实现此接口。
 * 音视频直播子频道是频道内用于实时语音通话、视频直播、屏幕共享等场景的特殊子频道类型。
 *
 * 该接口定义了音视频直播事件共有的核心属性：
 * - [live]：从事件中解析出的用户直播信息对象，包含进入/退出直播子频道的用户信息
 *
 * 作为事件体系中的抽象层，此接口本身不会被实例化，而是由具体的直播事件
 * （如用户进入直播子频道、用户退出直播子频道事件）来实现。
 *
 * @property live 直播房间的用户信息对象，包含用户ID、频道组ID、子频道ID等标识信息
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * @see UserEnteredChannelLiveEvent 用户进入音视频/直播子频道事件
 * @see UserExitChannelLiveEvent 用户退出音视频/直播子频道事件
 */
@EventAnnotation.EventMetaType("Not_MetaType_LiveRoomEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
interface LiveRoomEvent : ChannelEvent {
    override val metadataType: String
        get() = "Not_MetaType_LiveRoomEvent"

    val live: UserLive
}
