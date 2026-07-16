package io.github.zimoyin.qqbot.event.events.platform

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.event.events.Event
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler

/**
 * 平台事件根接口。
 *
 * 平台事件是全局级别的事件，通过全局事件总线（Global EventBus）进行传播和分发。
 * 与普通的本地事件不同，平台事件及其子事件**大多数无法被 Bot 的本地监听器捕获**，
 * 必须在全局事件总线上注册监听器才能收到。
 *
 * **重要行为说明：**
 * - 平台事件仅在全局事件总线中传播，Bot 本地监听器无法监听到大部分平台事件及其子事件。
 * - 所有叶子事件（即具体的事件类型）均为具体类（class），因此事件系统在使用时无需
 *   通过 {@link io.github.zimoyin.qqbot.annotation.EventAnnotation.EventHandler}
 *   注解指定的事件处理器对事件进行反序列化构建，框架会直接实例化具体类型。
 *
 * @author : zimo
 * @date : 2023/12/19
 * @see io.github.zimoyin.qqbot.event.events.Event
 */
@EventAnnotation.EventMetaType("Not_MetaType_PlatformEvent")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
interface PlatformEvent : Event
