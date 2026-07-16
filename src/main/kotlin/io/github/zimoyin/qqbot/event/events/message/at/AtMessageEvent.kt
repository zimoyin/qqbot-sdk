package io.github.zimoyin.qqbot.event.events.message.at

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import io.github.zimoyin.qqbot.event.handler.message.MessageHandler

/**
 * @机器人（@提及）消息事件标记接口。
 *
 * 此接口作为标记接口（Marker Interface），用于标识那些因用户 @机器人 而触发的消息事件。
 * 它本身不定义额外的属性和方法，仅用于在事件类型体系中对 @提及事件进行统一分类。
 *
 * 所有具体的 @消息事件（如 {@link GroupAtMessageEvent}、{@link ChannelAtMessageEvent}）
 * 都实现此接口，方便开发者通过类型判断来统一处理所有 @机器人场景。
 *
 * @author : zimo
 * @date : 2023/12/09
 */
@EventAnnotation.EventMetaType("Not_MetaType_AtMessageEvent")
@EventAnnotation.EventHandler(MessageHandler::class, true)
interface AtMessageEvent: MessageEvent
