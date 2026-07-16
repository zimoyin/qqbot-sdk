package io.github.zimoyin.qqbot.event.events.message.direct

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import io.github.zimoyin.qqbot.event.handler.message.MessageHandler

/**
 * 私聊/私信消息事件标记接口。
 *
 * 此接口作为标记接口（Marker Interface），用于标识所有私聊（Direct Message）类型的消息事件。
 * 它本身不定义额外的属性和方法，仅用于在事件类型体系中对私信事件进行统一分类。
 *
 * 所有具体的私信消息事件（如 {@link ChannelPrivateMessageEvent} 频道私信、
 * {@link UserPrivateMessageEvent} C2C单聊）都实现此接口，方便开发者通过类型判断
 * 来统一处理所有私信场景。
 *
 * @author : zimo
 * @date : 2023/12/09
 */
@EventAnnotation.EventMetaType("Not_MetaType_PrivateMessageEvent")
@EventAnnotation.EventHandler(MessageHandler::class, true)
interface PrivateMessageEvent : MessageEvent
