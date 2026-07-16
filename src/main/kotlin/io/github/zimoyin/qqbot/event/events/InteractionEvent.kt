package io.github.zimoyin.qqbot.event.events

import io.github.zimoyin.qqbot.LocalLogger
import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Contact
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.bot.message.MessageChainBuilder
import io.github.zimoyin.qqbot.bot.message.type.MessageItem
import io.github.zimoyin.qqbot.event.handler.message.InteractionEventHandler
import io.github.zimoyin.qqbot.net.bean.SendMessageResultBean
import io.github.zimoyin.qqbot.net.bean.message.InteractionEventData
import io.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import io.github.zimoyin.qqbot.net.http.api.replyInteractions
import io.github.zimoyin.qqbot.utils.ex.promise
import io.github.zimoyin.qqbot.utils.io
import io.vertx.core.Future
import kotlinx.coroutines.delay

/**
 * @author: zimo
 * @date:   2024/12/27
 *
 * 互动事件。
 *
 * 当用户与机器人的交互组件进行互动时触发，包括但不限于：
 * - 按钮点击（Button Click）
 * - 斜杠命令（Slash Command）
 * - 下拉选择（Select Menu）
 * - 模态框提交（Modal Submit）
 *
 * ## 确认机制（ok）
 *
 * QQ 互动事件要求客户端在收到事件后调用 [ok] 方法进行确认，通知服务器已收到事件。
 * 如果未在 **2.5 秒** 内调用 [ok]，该事件会在 `init` 块中自动调用 [ok] 方法进行确认，
 * 确保服务器不会因超时而重发或报错。
 *
 * ## 回复机制（reply）
 *
 * 通过 [reply] 方法可以对互动事件进行被动回复。支持文本、[MessageChain] 以及可变参数 [MessageItem] 三种形式。
 * 每次调用 [reply] 时 `msgSeq` 会自增，确保消息有序。
 *
 * ### 注意
 * 互动事件属于被动消息的一种，无法通过该事件进行主动消息推送。
 *
 * @see ok 互动确认方法
 * @see reply 互动回复方法
 */
@EventAnnotation.EventMetaType("INTERACTION_CREATE")
@EventAnnotation.EventHandler(InteractionEventHandler::class)
class InteractionEvent(
    override val metadataType: String = "INTERACTION_CREATE",
    override val metadata: String,
    override val botInfo: BotInfo,
    override val eventID: String,
    val windows: Contact,
    val data: InteractionEventData,
) : Event {
    /**
     * 标记 [ok] 方法是否已被调用。
     *
     * 用于 `init` 块中的超时检测：若 2.5 秒内未调用 [ok]，则自动调用以避免服务器超时。
     */
    private var isCallOk = false

    init {
        io {
            delay(2500)
            if (!isCallOk) {
                LocalLogger(InteractionEvent::class.java).error("InteractionEvent 未调用 ok() 方法，已自动调用")
                ok()
            }
        }
    }

    /**
     * 确认消息客户端已经收到互动事件。
     *
     * 调用此方法向 QQ 服务器返回一个状态码，表示客户端已收到并处理了该互动事件。
     * 如果在事件创建后 **2.5 秒** 内未手动调用此方法，框架会自动调用 `ok(0)` 进行确认。
     *
     * ### 返回码含义
     *
     * | 状态码 | 含义 |
     * |--------|------|
     * | 0 | 操作成功 |
     * | 1 | 操作失败 |
     * | 2 | 操作频繁 |
     * | 3 | 重复操作 |
     * | 4 | 没有权限 |
     * | 5 | 仅管理员操作 |
     *
     * @param code 返回的状态码，默认为 0（操作成功）
     * @return 异步操作结果，成功时完成 [Void]
     */
    fun ok(code: Int = 0): Future<Void> {
        isCallOk = true
        return HttpAPIClient.replyInteractions(botInfo.token, data.id, code)
    }

    /**
     * 消息序号。
     *
     * 每次调用 [reply] 时自动递增，确保回复消息的顺序性。
     * 初始值为 1。
     */
    var msgSeq: Int = 1

    /**
     * 以纯文本形式回复互动事件。
     *
     * 将文本封装为 [MessageChain] 后调用 [windows.send] 进行回复，
     * 自动附加 `msgSeq` 和 `eventID`。
     *
     * @param msg 回复的文本内容
     * @return 异步操作的 [Future]，成功时包含 [SendMessageResultBean] 结果信息
     */
    fun reply(msg: String): Future<SendMessageResultBean> {
        return reply(MessageChainBuilder().appendMeqSeq(msgSeq++).append(msg).build())
    }

    /**
     * 以 [MessageChain] 形式回复互动事件。
     *
     * 支持包含图片、表情、@提及等复杂消息结构的回复。
     * 如果 [MessageChain.replyEventID] 为空，则使用当前事件的 [eventID] 作为回复目标。
     *
     * @param message 要回复的 [MessageChain] 消息链
     * @return 异步操作的 [Future]，成功时包含 [SendMessageResultBean] 结果信息。
     *         如果 [eventID] 和 [message.replyEventID] 均为空，则返回一个失败的 Future
     * @throws io.vertx.core.Future 当 eventID 为空时返回失败的 Future
     */
    fun reply(message: MessageChain): Future<SendMessageResultBean> {
        val eventID = if (message.replyEventID.isNullOrEmpty()) {
            eventID
        } else {
            message.replyEventID
        }
        if (eventID.isEmpty()) return promise<SendMessageResultBean>().apply {
            tryFail("eventID is null")
        }.future()
        return windows.send(
            MessageChainBuilder().appendMeqSeq(msgSeq++).appendEventId(eventID).append(message).build()
        )
    }

    /**
     * 以可变参数 [MessageItem] 形式回复互动事件。
     *
     * 支持传入多个消息元素（如文本、图片、@提及等），
     * 自动组合为 [MessageChain] 并附加 `msgSeq` 和 `eventID` 后发送。
     *
     * @param items 要回复的消息元素列表
     * @return 异步操作的 [Future]，成功时包含 [SendMessageResultBean] 结果信息
     */
    fun reply(vararg items: MessageItem): Future<SendMessageResultBean> {
        return reply(MessageChainBuilder().appendMeqSeq(msgSeq++).appendEventId(eventID).appendItems(*items).build())
    }
}
