package io.github.zimoyin.qqbot.event.events.platform

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Contact
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler
import org.slf4j.LoggerFactory

/**
 * 消息发送前事件。
 *
 * 在消息**发送到服务器之前**触发。此事件为**全局事件**，仅在全局事件总线中传递。
 *
 * 与 {@link MessageSendEvent} 不同，此事件在消息实际发送之前触发，可以在消息
 * 进入发送流程前进行干预。但请注意：**不要直接在此事件中修改消息内容**——
 * 如需对发送的消息进行审核和拦截，请使用 {@link #interceptor} 静态方法注册拦截器。
 *
 * **拦截器机制：**
 * 通过 {@link #interceptor} 方法可以注册一个或多个拦截器函数，这些拦截器会在消息发送前
 * 被依次调用（链式处理）。每个拦截器接收当前的 {@code MessageSendPreEvent} 实例，
 * 返回处理后的实例。如果某个拦截器将 {@code intercept} 属性设置为 {@code true}，
 * 则该消息将被阻止发送。
 *
 * **使用示例：**
 * ```
 * // 注册一个拦截器，拦截所有消息
 * MessageSendPreEvent.interceptor {
 *     return@interceptor it.apply {
 *         intercept = true
 *     }
 * }
 *
 * // 注册一个拦截器，修改消息内容
 * MessageSendPreEvent.interceptor {
 *     return@interceptor it.apply {
 *         messageChain = messageChain.append("审核通过")
 *     }
 * }
 * ```
 *
 * @param msgID 待发送消息的唯一标识符（消息ID）
 * @param messageChain 待发送的消息内容链，可在拦截器中修改
 * @param contact 消息发送的目标联系人/会话
 * @param botInfo 当前事件所属的机器人实例信息
 * @param eventID 事件唯一标识符
 * @param intercept 是否拦截此消息的发送，设置为 {@code true} 时消息将被阻止发送
 * @author : zimo
 * @date : 2023/12/19
 * @see MessageSendEvent
 * @see MessageSendInterceptEvent
 */
@EventAnnotation.EventMetaType("Platform_MessageSendPreEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
open class MessageSendPreEvent(
    override val metadata: String = "Platform_MessageSendPreEvent",
    override val metadataType: String = "Platform_MessageSendPreEvent",
    /**
     * 待发送消息的唯一标识符（消息ID）。
     */
    open val msgID: String,
    /**
     * 待发送的消息内容链。可在拦截器中修改此属性来变更即将发送的消息内容。
     */
    open var messageChain: MessageChain,
    /**
     * 消息发送的目标联系人/会话。
     */
    open val contact: Contact,
    override val botInfo: BotInfo = contact.botInfo,
    override val eventID: String ="",
    /**
     * 是否拦截此消息的发送。
     *
     * 当设置为 {@code true} 时，该消息将被阻止发送到服务器。
     * 在拦截器链式处理中，任一拦截器将其设为 {@code true} 即会导致最终拦截。
     */
    var intercept: Boolean = false,
) : PlatformEvent {
    companion object {
        /**
         * 消息发送前拦截器注册表。
         *
         * 存储所有已注册的拦截器函数，按注册顺序依次执行。
         */
        private val Interceptors = ArrayList<(event: MessageSendPreEvent) -> MessageSendPreEvent>()

        /**
         * 注册一个消息发送前拦截器。
         *
         * 拦截器函数接收当前的 {@code MessageSendPreEvent} 实例，应返回处理后的实例。
         * 如果拦截器返回的实例中 {@code intercept = true}，则该消息将被阻止发送。
         *
         * 多个拦截器按注册顺序依次执行（链式处理）。每个拦截器都可以：
         * - 修改消息内容（通过 {@code messageChain} 属性）
         * - 决定是否拦截（通过 {@code intercept} 属性）
         *
         * @param call 拦截器函数，接收并返回 {@code MessageSendPreEvent}
         */
        fun interceptor(call: (event: MessageSendPreEvent) -> MessageSendPreEvent) {
            Interceptors.add(call)
        }

        /**
         * 执行所有已注册的拦截器，返回最终处理结果。
         *
         * 按注册顺序依次执行所有拦截器。如果任一拦截器抛出异常，
         * 该异常会被捕获并记录错误日志，不会中断后续拦截器的执行。
         *
         * 拦截逻辑：
         * - 只要任一拦截器设置了 {@code intercept = true}，最终结果即为拦截。
         * - 如果没有任何拦截器拦截，最后一个未拦截拦截器的修改结果将作为最终事件。
         *
         * @param event 原始的 {@code MessageSendPreEvent} 实例
         * @return 经过所有拦截器处理后的 {@code MessageSendPreEvent} 实例
         */
        fun result(event: MessageSendPreEvent): MessageSendPreEvent {
            var intercept = false
            var event2: MessageSendPreEvent? = null
            Interceptors.forEach {
                kotlin.runCatching {
                    val preEvent = it(event)
                    if (!preEvent.intercept) event2 = preEvent
                    intercept = intercept || preEvent.intercept
                }.onFailure {
                    LoggerFactory.getLogger(MessageSendPreEvent::class.java)
                        .error("MessageSendPreEvent interceptor error", it)
                }
            }
            event2?.intercept = intercept
            return event
        }
    }
}
