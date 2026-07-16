package io.github.zimoyin.qqbot.bot.message

import io.github.zimoyin.qqbot.bot.message.type.MessageItem
import io.github.zimoyin.qqbot.bot.message.type.PlainTextMessage
import io.github.zimoyin.qqbot.bot.message.type.ProactiveMediaMessage
import io.github.zimoyin.qqbot.bot.message.type.ReferenceMessage
import io.github.zimoyin.qqbot.event.events.Event
import io.github.zimoyin.qqbot.net.bean.message.send.MediaMessageBean

/**
 * 消息链构造器（MessageChainBuilder）。
 *
 * 通过流式 API（Fluent API / Builder 模式）构建 [{@link MessageChain}] 的辅助类。
 * 提供了链式调用方法，可依次添加文本、图片、引用、表情等各类消息项，
 * 最后通过 [{@link build}] 方法生成完整的 MessageChain。
 *
 * **设计目的：**
 * MessageChain 内部使用不可变集合管理消息项，不适合频繁修改。
 * MessageChainBuilder 封装了构建过程，提供直观的流式接口。
 *
 * **典型用法：**
 * ```
 * // 构建一条带引用的回复消息
 * val chain = MessageChainBuilder(originalMsg.id)
 *     .reference(originalMsg.id)      // 引用原消息
 *     .append("收到你的消息！")         // 添加文本
 *     .appendMeqSeq(2)                // 设置消息序号
 *     .build()
 *
 * // 构建一条主动消息
 * val chain = MessageChainBuilder()
 *     .append("主动推送通知")
 *     .append(ImageMessage(imageUrl))
 *     .build()
 * ```
 *
 * @param id 消息的唯一标识符（可选）。
 *           发送被动回复时传入被回复消息的 ID；发送主动消息时可为 null。
 *
 * @author : zimo
 * @date : 2023/12/09
 *
 * @see MessageChain
 * @see MessageItem
 */
class MessageChainBuilder(private var id: String? = null) {
    /**
     * 消息的回复事件 ID，用于关联回复链路。
     */
    private var eventId: String? = null
    /**
     * 消息序号，用于保证多条被动回复的顺序性。
     */
    private var msgSeq = 1

    /**
     * 基于现有 MessageChain 创建构造器。
     *
     * 新构造器继承原消息链的 [id]，并预先填充原消息链中的所有消息项。
     *
     * @param chain 源消息链
     */
    constructor(chain: MessageChain) : this(chain.id) {
        append(chain)
    }

    /**
     * 消息项的内部存储列表。
     */
    private val internalItems: ArrayList<MessageItem> = ArrayList()

    /**
     * 设置消息的唯一标识符。
     *
     * 发送被动回复时，应设置为被回复消息的 ID。
     *
     * @param id 消息 ID
     * @return 当前 Builder 实例（链式调用）
     */
    fun setID(id: String): MessageChainBuilder {
        this.id = id
        return this
    }

    /**
     * 获取当前消息的唯一标识符。
     *
     * @return 消息 ID
     * @throws NullPointerException 如果未设置 id
     */
    fun getID(): String {
        return id!!
    }

    /**
     * 添加一条对指定消息的引用。
     *
     * 在消息链中插入一个 [{@link ReferenceMessage}]，使发送的消息成为对目标消息的引用回复。
     *
     * @param id 被引用消息的 ID
     * @return 当前 Builder 实例（链式调用）
     */
    fun reference(id: String): MessageChainBuilder {
        internalItems.add(ReferenceMessage(id))
        return this
    }

    /**
     * 添加一条对指定消息链对应消息的引用。
     *
     * 等效于 `reference(chain.id!!)`。
     *
     * @param chain 被引用的消息链
     * @return 当前 Builder 实例（链式调用）
     */
    fun reference(chain: MessageChain): MessageChainBuilder {
        internalItems.add(ReferenceMessage(chain.id!!))
        return this
    }

    /**
     * 向消息链末尾追加一个消息项。
     *
     * @param item 要追加的 [MessageItem] 实例
     * @return 当前 Builder 实例（链式调用）
     */
    fun append(item: MessageItem): MessageChainBuilder {
        internalItems.add(item)
        return this
    }

    /**
     * 向消息链末尾批量追加多个消息项。
     *
     * @param item 要追加的 [MessageItem] 可变参数
     * @return 当前 Builder 实例（链式调用）
     */
    fun appendItems(vararg item: MessageItem): MessageChainBuilder {
        internalItems.addAll(item)
        return this
    }

    /**
     * 向消息链末尾追加一段纯文本。
     *
     * 内部会将字符串包装为 [{@link PlainTextMessage}]。
     *
     * @param text 要追加的文本内容
     * @return 当前 Builder 实例（链式调用）
     */
    fun append(text: String): MessageChainBuilder {
        internalItems.add(PlainTextMessage(text))
        return this
    }

    /**
     * 向消息链末尾追加一个主动媒体消息项。
     *
     * 使用 [{@link MediaMessageBean}] 构建的 [{@link ProactiveMediaMessage}]，
     * 适用于主动推送图片/音频/视频等富媒体。
     *
     * @param m 媒体消息的元数据 Bean
     * @return 当前 Builder 实例（链式调用）
     */
    fun append(m: MediaMessageBean): MessageChainBuilder {
        internalItems.add(ProactiveMediaMessage(m))
        return this
    }

    /**
     * 将另一个消息链中的所有消息项追加到当前消息链末尾。
     *
     * @param chain 源消息链
     * @return 当前 Builder 实例（链式调用）
     */
    fun append(chain: MessageChain): MessageChainBuilder {
        chain.forEach {
            internalItems.add(it)
        }
        return this
    }

    /**
     * 设置回复目标事件。
     *
     * 从事件中提取 eventID，使此消息成为对特定事件的回复。
     *
     * @param eventId0 目标事件，其 [Event.eventID] 将被提取
     * @return 当前 Builder 实例（链式调用）
     */
    fun append(eventId0: Event?): MessageChainBuilder {
        eventId = eventId0?.eventID
        return this
    }

    /**
     * 设置回复目标事件的事件 ID。
     *
     * @param eventId0 目标事件的 ID 字符串
     * @return 当前 Builder 实例（链式调用）
     */
    fun appendEventId(eventId0: String?): MessageChainBuilder {
        eventId = eventId0
        return this
    }

    /**
     * 设置回复目标事件（通过事件对象）。
     *
     * 等效于 `appendEventId(eventId0?.eventID)`。
     *
     * @param eventId0 目标事件，其 [Event.eventID] 将被提取
     * @return 当前 Builder 实例（链式调用）
     */
    fun appendEventId(eventId0: Event?): MessageChainBuilder {
        eventId = eventId0?.eventID
        return this
    }

    /**
     * 构建消息链的原始文本（元文本）。
     *
     * 遍历所有已添加的消息项，调用每个消息项的 [MessageItem.toMetaContent] 方法
     * 拼接为一个字符串。此字符串作为 [{@link MessageChain.metaTextContent}] 的值。
     *
     * @return 消息链的元文本内容
     */
    fun buildMetaTextContent(): String {
        val sb = StringBuilder()
        internalItems.forEach {
            sb.append(it.toMetaContent())
        }
        return sb.toString()
    }

    /**
     * 构建最终的 [{@link MessageChain}] 实例。
     *
     * 将所有已设置的参数（id、消息项列表、事件 ID、消息序号）以及自动生成的
     * [metaTextContent] 传入 MessageChain 构造函数，返回构建完成的不可变消息链。
     *
     * @return 构建完成的 MessageChain 实例
     */
    fun build(): MessageChain {
        return MessageChain(
            id = id,
            metaTextContent = buildMetaTextContent(),
            internalItems = internalItems,
            replyEventID = eventId,
            msgSeq = msgSeq
        )
    }

    /**
     * 设置消息序号。
     *
     * 消息序号用于保证多条被动回复消息的顺序性。
     * 通常在使用事件回复（如 [{@link io.github.zimoyin.qqbot.event.events.message.MessageEvent.reply}]）
     * 时由 SDK 自动管理，一般不需要手动设置。
     *
     * @param msgSeq0 消息序号值
     * @return 当前 Builder 实例（链式调用）
     */
    fun appendMeqSeq(msgSeq0: Int): MessageChainBuilder {
        msgSeq = msgSeq0
        return this
    }
}
