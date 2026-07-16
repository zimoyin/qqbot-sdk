package io.github.zimoyin.qqbot.event.events

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.event.events.platform.PlatformEvent
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler
import io.github.zimoyin.qqbot.event.handler.message.MessageAuditPassHandler
import io.github.zimoyin.qqbot.event.handler.message.MessageAuditRejectHandler
import io.github.zimoyin.qqbot.net.bean.MessageAuditBean


/**
 * 消息审核事件根接口。
 *
 * 定义消息审核流程中所有事件的基础类型。消息审核是 QQ Bot 平台提供的
 * 内容安全机制，允许开发者在消息实际发送到接收方之前对消息内容进行审核。
 *
 * **审核事件流程：**
 * 1. 用户发送消息 → 触发 {@link MessageStartAuditEvent}（平台事件，全局传播）
 * 2. 审核通过 → 触发 {@link MessageAuditPassEvent}（MESSAGE_AUDIT_PASS）
 * 3. 审核不通过 → 触发 {@link MessageAuditRejectEvent}（MESSAGE_AUDIT_REJECT）
 *
 * @author : zimo
 * @date : 2023/12/20
 * @see MessageStartAuditEvent
 * @see MessageAuditPassEvent
 * @see MessageAuditRejectEvent
 */
@EventAnnotation.EventMetaType("Platform_MessageAuditEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
interface MessageAuditEvent : Event {
    override val botInfo: BotInfo
    override val metadata: String
    override val metadataType: String
}

/**
 * 消息审核开始事件。
 *
 * 当消息进入审核流程时触发，属于平台事件，通过全局事件总线传播。
 * 此事件表示消息已被提交至审核系统，等待审核结果。
 *
 * 该事件同时实现了 {@link PlatformEvent} 和 {@link MessageAuditEvent} 接口，
 * 因此既可以在全局总线上监听，也可以作为审核事件系列的一部分处理。
 *
 * **注意：** 此事件只是通知消息已进入审核流程，不携带审核结果。
 * 审核结果请监听 {@link MessageAuditPassEvent} 或 {@link MessageAuditRejectEvent}。
 *
 * @param botInfo 当前事件所属的机器人实例信息
 * @param metadata 事件元数据
 * @param metadataType 事件类型标识符，固定为 "Platform_MessageAuditEvent"
 * @param eventID 事件唯一标识符
 * @author : zimo
 * @date : 2023/12/20
 * @see MessageAuditEvent
 * @see MessageAuditPassEvent
 * @see MessageAuditRejectEvent
 */
@EventAnnotation.EventMetaType("Platform_MessageStartAuditEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
data class MessageStartAuditEvent(
    override val botInfo: BotInfo,
    override val metadata: String = "Platform_MessageAuditEvent",
    override val metadataType: String = "Platform_MessageAuditEvent",
    override val eventID: String ="",
) : PlatformEvent, MessageAuditEvent

/**
 * 消息审核通过事件（MESSAGE_AUDIT_PASS）。
 *
 * 当消息通过内容审核时触发。对应 QQ Bot API 的 {@code MESSAGE_AUDIT_PASS} 事件类型。
 *
 * 此事件携带 {@code message} 字段，包含审核结果的详细信息，
 * 如审核通过的消息ID、审核时间、审核建议等。
 *
 * **典型使用场景：**
 * - 记录审核通过的日志
 * - 在审核通过后执行后续业务逻辑（如通知发送方消息已通过审核）
 * - 统计分析审核通过率
 *
 * @param botInfo 当前事件所属的机器人实例信息
 * @param metadata 事件元数据，通常包含服务器返回的完整 JSON 载荷
 * @param metadataType 事件类型标识符，固定为 "MESSAGE_AUDIT_PASS"
 * @param message 审核结果实体，包含审核状态、审核建议等详细信息
 * @param eventID 事件唯一标识符
 * @author : zimo
 * @date : 2023/12/20
 * @see MessageAuditEvent
 * @see MessageAuditRejectEvent
 */
@EventAnnotation.EventMetaType("MESSAGE_AUDIT_PASS")
@EventAnnotation.EventHandler(MessageAuditPassHandler::class)
data class MessageAuditPassEvent(
    override val botInfo: BotInfo,
    override val metadata: String,
    override val metadataType: String = "MESSAGE_AUDIT_PASS",
    /**
     * 审核结果实体，包含审核通过状态及审核建议等详细信息。
     */
    val message: MessageAuditBean,
    override val eventID: String ="",
) : MessageAuditEvent


/**
 * 消息审核不通过（拒绝）事件（MESSAGE_AUDIT_REJECT）。
 *
 * 当消息未通过内容审核时触发。对应 QQ Bot API 的 {@code MESSAGE_AUDIT_REJECT} 事件类型。
 *
 * 此事件携带 {@code message} 字段，包含审核拒绝的原因和建议。
 * 开发者可根据 {@code message} 中的信息向消息发送者反馈不通过原因。
 *
 * **典型使用场景：**
 * - 记录审核拒绝的日志
 * - 向消息发送者返回审核不通过的原因
 * - 统计分析审核拒绝率和常见拒绝原因
 * - 触发人工复审流程
 *
 * @param botInfo 当前事件所属的机器人实例信息
 * @param metadata 事件元数据，通常包含服务器返回的完整 JSON 载荷
 * @param metadataType 事件类型标识符，固定为 "MESSAGE_AUDIT_REJECT"
 * @param message 审核结果实体，包含审核拒绝原因、建议等详细信息
 * @param eventID 事件唯一标识符
 * @author : zimo
 * @date : 2023/12/20
 * @see MessageAuditEvent
 * @see MessageAuditPassEvent
 */
@EventAnnotation.EventMetaType("MESSAGE_AUDIT_REJECT")
@EventAnnotation.EventHandler(MessageAuditRejectHandler::class)
data class MessageAuditRejectEvent(
    override val botInfo: BotInfo,
    override val metadata: String,
    override val metadataType: String = "MESSAGE_AUDIT_REJECT",
    /**
     * 审核结果实体，包含审核拒绝原因及建议等详细信息。
     */
    val message: MessageAuditBean,
    override val eventID: String ="",
) : MessageAuditEvent
