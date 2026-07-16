package io.github.zimoyin.qqbot.event.events

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler

/**
 * 用户自定义事件的抽象基类。
 *
 * 用于定义不在 SDK 内置事件范围内的自定义事件类型。
 * 继承此类后，开发者可以创建自己的事件并通过事件总线进行发布和监听。
 *
 * [metadataType] 和 [metadata] 均默认使用当前类的全限定名（[javaClass.name]）作为值，
 * 无需手动指定，自动区别于其他事件类型。
 *
 * 注意：该事件的 [EventAnnotation.EventHandler] 注解指定了 [NoneEventHandler]，
 * 表示不需要 SDK 内置的事件处理器对其进行构造，完全由用户自行创建和发布。
 *
 * @author : zimo
 * @date : 2024/01/02
 */
@EventAnnotation.EventMetaType("Not_MetaType_CustomEvents")
@EventAnnotation.EventHandler(NoneEventHandler::class, true)
abstract class CustomEvent : Event {
  /**
   * 事件类型标识符。
   *
   * 默认取值为当前类的全限定名（[javaClass.name]），
   * 自动区分不同的自定义事件类型，无需子类手动覆盖。
   */
  override val metadataType: String
    get() = this.javaClass.name

  /**
   * 事件的元数据/原始数据。
   *
   * 默认取值为当前类的全限定名（[javaClass.name]），
   * 对于自定义事件，通常不需要 JSON 原始数据，因此使用类名作为占位值。
   */
  override val metadata: String
    get() = this.javaClass.name
}
