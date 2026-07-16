package io.github.zimoyin.qqbot.event.events.friend

import io.github.zimoyin.qqbot.annotation.EventAnnotation

/**
 * 好友变更事件的标记接口。
 *
 * 作为好友列表变更相关事件的抽象基类，所有涉及好友关系变化的事件
 * （如用户添加机器人好友 [AddFriendEvent]、用户删除机器人好友 [DeleteFriendEvent]）
 * 均继承此接口。
 *
 * 该接口本身不定义额外字段，仅用于在事件类型层级中进行分类和筛选。
 * 使用了 [EventAnnotation.EventHandler] 注解并设置 `ignore = true`，
 * 不直接参与事件处理器的匹配。
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("Not_MetaType_FriendUpdateEvent")
@EventAnnotation.EventHandler(ignore = true)
interface FriendUpdateEvent : FriendEvent
