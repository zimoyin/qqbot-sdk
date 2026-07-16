package io.github.zimoyin.qqbot.event.events.channel.sub

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.contact.channel.ChannelImpl
import io.github.zimoyin.qqbot.event.events.channel.ChannelEvent
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler
import io.github.zimoyin.qqbot.net.bean.ChannelBean


/**
 * 子频道事件的基类接口。
 *
 * 所有与子频道（频道内的分组频道）相关的事件均直接或间接实现此接口。
 * 子频道是频道服务器下的二级分组单位，包括文字子频道、语音子频道、直播子频道等。
 *
 * 该接口定义了子频道事件共有的核心属性：
 * - [channelBean]：从事件中解析出的频道信息 Bean，包含频道ID、所属频道组ID等标识信息
 * - [channel]：根据 [channelBean] 自动转换生成的 [Channel] 对象，用于后续业务操作（如发送消息等）
 *
 * 作为事件体系中的抽象层，此接口本身不会被实例化，而是由具体的子频道事件
 * （如子频道创建、更新、删除事件）来实现。
 *
 * @property channelBean 从事件数据中解析出的频道信息 Bean 对象，包含子频道的完整属性信息
 * @property channel 事件所属的子频道对象，根据 [channelBean] 中的频道组ID和子频道ID自动构建
 *
 * @author : zimo
 * @date : 2023/12/20
 *
 * @see SubChannelCreateEvent 子频道创建事件
 * @see SubChannelUpdateEvent 子频道更新事件
 * @see SubChannelDeleteEvent 子频道删除事件
 */
@EventAnnotation.EventMetaType("Not_MetaType_SubChannelEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
interface SubChannelEvent : ChannelEvent {
    override val metadataType: String
        get() = "Not_MetaType_SubChannelEvent"

    val channelBean: ChannelBean

    override val channel: Channel
        get() = ChannelImpl.convert(botInfo,channelBean.guildID,channelBean.channelID,channelBean.channelID)
}
