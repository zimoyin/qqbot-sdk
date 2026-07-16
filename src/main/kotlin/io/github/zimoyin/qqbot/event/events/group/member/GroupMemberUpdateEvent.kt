package io.github.zimoyin.qqbot.event.events.group.member

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.event.events.group.GroupEvent

/**
 * 群成员变更事件的标记接口。
 *
 * 作为群成员相关变更事件的抽象基类，所有涉及群成员增减的事件（如机器人加入群聊 [AddGroupEvent]、
 * 机器人退出群聊 [ExitGroupEvent]）均继承此接口。
 *
 * 该接口本身不定义额外字段，仅用于在事件类型层级中进行分类和筛选。
 * 使用了 [EventAnnotation.EventHandler] 注解并设置 `ignore = true`，
 * 不直接参与事件处理器的匹配。
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("Not_MetaType_GroupMemberUpdate")
@EventAnnotation.EventHandler(ignore = true)
interface GroupMemberUpdateEvent : GroupEvent
