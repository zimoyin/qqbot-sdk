package io.github.zimoyin.qqbot.event.events.message.direct

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.contact.ChannelPrivateUser
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.events.message.ChannelMessageEvent
import io.github.zimoyin.qqbot.event.handler.message.ChannelPrivateMessageHandler
import io.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import io.github.zimoyin.qqbot.net.http.api.channel.recallChannelPrivateMessage
import io.vertx.core.Future

/**
 * 频道私信消息事件。
 *
 * 当用户在频道中向机器人发送**私信（私聊）**时触发此事件，事件元数据类型为 {@code DIRECT_MESSAGE_CREATE}。
 *
 * 此事件同时继承了 {@link PrivateMessageEvent} 标记接口和 {@link ChannelMessageEvent}，
 * 因此即可以被归类为私信事件进行统一处理，也拥有频道消息的完整能力（如表情反应管理）。
 *
 * **撤回行为的区别：**
 * 此事件重写了父类 {@link ChannelMessageEvent} 的 {@link #recall()} 和 {@link #recall(Boolean)} 方法，
 * 使用频道私信专用API（{@code recallChannelPrivateMessage}）来撤回消息，而非频道公共消息的撤回API。
 * 这是必要的，因为频道私信与频道公共消息属于不同的消息体系。
 *
 * **典型使用场景：** 用户在频道中点击机器人头像进入私聊，发送消息给机器人。
 *
 * @author : zimo
 * @date : 2023/12/11
 */
@EventAnnotation.EventMetaType("DIRECT_MESSAGE_CREATE")
@EventAnnotation.EventHandler(ChannelPrivateMessageHandler::class)
class ChannelPrivateMessageEvent(
    /**
     * 事件元数据原始字符串，包含事件相关的附加信息。
     */
    override val metadata: String,

    /**
     * 消息的唯一标识符。
     */
    override val msgID: String,

    /**
     * 消息来源的频道会话窗口（即使为私信，窗口类型仍为 {@link Channel}）。
     */
    override val windows: Channel,

    /**
     * 消息内容链，包含消息的完整内容。
     */
    override val messageChain: MessageChain,

    /**
     * 消息发送者（频道私密用户）。
     */
    override val sender: ChannelPrivateUser,

    /**
     * 当前机器人信息。
     */
    override val botInfo: BotInfo,

    /**
     * 关联的频道对象，默认与 {@code windows} 相同。
     */
    override val channel: Channel = windows,

    /**
     * 事件唯一标识符，用于跟踪和去重。
     */
    override val eventID: String = "",

    /**
     * 消息序号计数器，初始值为 1，用于被动回复的顺序控制。
     */
    override var msgSeq: Int = 1,
) : PrivateMessageEvent, ChannelMessageEvent {
    /**
     * 事件元数据类型，固定为 {@code "DIRECT_MESSAGE_CREATE"}。
     */
    override val metadataType: String = "DIRECT_MESSAGE_CREATE"

    /**
     * 撤回当前频道私信消息（默认显示撤回提示）。
     *
     * 重写了父类 {@link ChannelMessageEvent#recall()} 方法，使用频道私信专用撤回API
     * （{@code recallChannelPrivateMessage}），而非频道公共消息的撤回API。
     * 等同于调用 {@code recall(false)}，撤回后会显示撤回提示小灰条。
     *
     * @return 异步结果，操作成功时返回 {@code true}
     */
    @OptIn(UntestedApi::class)
    override fun recall(): Future<Boolean> {
        return recall(false)
    }

    /**
     * 撤回当前频道私信消息。
     *
     * 重写了父类 {@link ChannelMessageEvent#recall(Boolean)} 方法，使用频道私信专用撤回API
     * （{@code recallChannelPrivateMessage}），而非频道公共消息的撤回API。
     *
     * @param hidetip 是否隐藏撤回提示（小灰条）。{@code true} 表示不显示撤回提示，{@code false} 表示显示提示
     * @return 异步结果，操作成功时返回 {@code true}
     */
    @OptIn(UntestedApi::class)
    override fun recall(hidetip: Boolean): Future<Boolean> {
        return HttpAPIClient.recallChannelPrivateMessage(channel, msgID, hidetip)
    }
}
