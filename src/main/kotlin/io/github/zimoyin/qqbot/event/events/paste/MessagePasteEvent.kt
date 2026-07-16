package io.github.zimoyin.qqbot.event.events.paste

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.event.events.Event
import io.github.zimoyin.qqbot.net.bean.message.MessageReaction

/**
 * 消息表态（Reaction）事件根接口。
 *
 * 当用户对某条消息添加或移除表态（即表情反应/Emoji Reaction）时触发。
 * 这是 QQ 频道中的"消息表态"功能的抽象基类。
 *
 * **子事件类型：**
 * - {@link MessageAddPasteEvent} — 用户添加了表态（MESSAGE_REACTION_ADD）
 * - {@link MessageDeletePasteEvent} — 用户移除了表态（MESSAGE_REACTION_REMOVE）
 *
 * **重要说明：**
 * - "表态"是 QQ 频道中用户对消息的快捷反馈方式，类似于其他平台中的 Emoji Reaction。
 * - {@code message} 字段包含了表态的详细信息，包括目标消息、表态表情和操作用户等。
 *
 * @author : zimo
 * @date : 2023/12/20
 * @see MessageAddPasteEvent
 * @see MessageDeletePasteEvent
 */
@EventAnnotation.EventMetaType("Not_MetaType_MessagePasteEvent")
@EventAnnotation.EventHandler(ignore = true)
interface MessagePasteEvent : Event {
    /**
     * 表态信息实体。
     *
     * 包含目标消息ID、表态的 Emoji 类型、操作用户信息等表态相关的完整数据。
     */
    val message: MessageReaction
}
