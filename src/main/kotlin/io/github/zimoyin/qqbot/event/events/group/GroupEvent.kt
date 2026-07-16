package io.github.zimoyin.qqbot.event.events.group

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.event.events.Event
import java.util.*

/**
 * 群聊相关事件的基类接口。
 *
 * 所有群聊相关的事件（如机器人加入群聊、退出群聊、群管理员操作等）均直接或间接继承此接口。
 * 该接口定义了群聊事件的基础字段，继承自 [Event]。
 *
 * 注意：该接口使用了 [EventAnnotation.EventHandler] 注解并设置 `ignore = true`，
 * 因此它不会直接参与事件处理器的匹配，仅作为类型继承层次中的基类使用。
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("Not_MetaType_GroupEvent")
@EventAnnotation.EventHandler(ignore = true)
interface GroupEvent : Event {
    /**
     * 群聊的唯一标识符（群号）。
     *
     * 由 QQ 服务器下发，用于唯一标识一个群聊会话。
     */
    val groupID :String

    /**
     * 操作者的成员 OpenID。
     *
     * 表示触发该群聊事件的操作成员标识。当无法确定具体的操作者时，
     * 默认返回 `"none"`。
     */
    val opMemberOpenid :String
        get() = "none"

    /**
     * 事件发生的时间戳。
     *
     * 记录事件在服务器端产生的时间。默认实现返回当前系统时间 [Date]。
     * 子类在构造时由事件数据填充具体的服务器时间。
     */
    val timestamp : Date
        get() = Date()
}
