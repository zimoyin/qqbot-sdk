package io.github.zimoyin.qqbot.event.supporter

import io.github.zimoyin.qqbot.LocalLogger
import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.event.events.*
import io.github.zimoyin.qqbot.event.events.bot.BotReadyEvent
import io.github.zimoyin.qqbot.event.events.bot.BotResumedEvent
import io.github.zimoyin.qqbot.event.events.bot.BotStatusEvent
import io.github.zimoyin.qqbot.event.events.channel.ChannelEvent
import io.github.zimoyin.qqbot.event.events.channel.forum.ForumEvent
import io.github.zimoyin.qqbot.event.events.channel.forum.audit.ForumPublishAuditEvent
import io.github.zimoyin.qqbot.event.events.channel.forum.post.ForumPostCreateEvent
import io.github.zimoyin.qqbot.event.events.channel.forum.post.ForumPostDeleteEvent
import io.github.zimoyin.qqbot.event.events.channel.forum.post.ForumPostEvent
import io.github.zimoyin.qqbot.event.events.channel.forum.reply.ForumReplyCreateEvent
import io.github.zimoyin.qqbot.event.events.channel.forum.reply.ForumReplyDeleteEvent
import io.github.zimoyin.qqbot.event.events.channel.forum.reply.ForumReplyEvent
import io.github.zimoyin.qqbot.event.events.channel.forum.thread.ForumThreadCreateEvent
import io.github.zimoyin.qqbot.event.events.channel.forum.thread.ForumThreadDeleteEvent
import io.github.zimoyin.qqbot.event.events.channel.forum.thread.ForumThreadEvent
import io.github.zimoyin.qqbot.event.events.channel.forum.thread.ForumThreadUpdateEvent
import io.github.zimoyin.qqbot.event.events.channel.guild.BotGuildUpdatedEvent
import io.github.zimoyin.qqbot.event.events.channel.guild.BotJoinedGuildEvent
import io.github.zimoyin.qqbot.event.events.channel.guild.BotLeftGuildEvent
import io.github.zimoyin.qqbot.event.events.channel.guild.GuildOperate
import io.github.zimoyin.qqbot.event.events.channel.member.GuildMemberAddEvent
import io.github.zimoyin.qqbot.event.events.channel.member.GuildMemberDeleteEvent
import io.github.zimoyin.qqbot.event.events.channel.member.GuildMemberEvent
import io.github.zimoyin.qqbot.event.events.channel.member.GuildMemberUpdateEvent
import io.github.zimoyin.qqbot.event.events.channel.sub.SubChannelCreateEvent
import io.github.zimoyin.qqbot.event.events.channel.sub.SubChannelDeleteEvent
import io.github.zimoyin.qqbot.event.events.channel.sub.SubChannelEvent
import io.github.zimoyin.qqbot.event.events.channel.sub.SubChannelUpdateEvent
import io.github.zimoyin.qqbot.event.events.friend.*
import io.github.zimoyin.qqbot.event.events.group.GroupEvent
import io.github.zimoyin.qqbot.event.events.group.member.AddGroupEvent
import io.github.zimoyin.qqbot.event.events.group.member.ExitGroupEvent
import io.github.zimoyin.qqbot.event.events.group.member.GroupMemberUpdateEvent
import io.github.zimoyin.qqbot.event.events.group.operation.CloseGroupBotEvent
import io.github.zimoyin.qqbot.event.events.group.operation.GroupBotOperationEvent
import io.github.zimoyin.qqbot.event.events.group.operation.OpenGroupBotEvent
import io.github.zimoyin.qqbot.event.events.message.MessageEvent
import io.github.zimoyin.qqbot.event.events.message.PrivateChannelMessageEvent
import io.github.zimoyin.qqbot.event.events.message.at.AtMessageEvent
import io.github.zimoyin.qqbot.event.events.message.at.ChannelAtMessageEvent
import io.github.zimoyin.qqbot.event.events.message.at.GroupAtMessageEvent
import io.github.zimoyin.qqbot.event.events.message.direct.ChannelPrivateMessageEvent
import io.github.zimoyin.qqbot.event.events.message.direct.PrivateMessageEvent
import io.github.zimoyin.qqbot.event.events.message.direct.UserPrivateMessageEvent
import io.github.zimoyin.qqbot.event.events.paste.MessageAddPasteEvent
import io.github.zimoyin.qqbot.event.events.paste.MessageDeletePasteEvent
import io.github.zimoyin.qqbot.event.events.paste.MessagePasteEvent
import io.github.zimoyin.qqbot.event.events.platform.*
import io.github.zimoyin.qqbot.event.events.platform.bot.BotHelloEvent
import io.github.zimoyin.qqbot.event.events.platform.bot.BotOfflineEvent
import io.github.zimoyin.qqbot.event.events.platform.bot.BotOnlineEvent
import io.github.zimoyin.qqbot.event.events.platform.bot.BotReconnectNotificationEvent
import io.github.zimoyin.qqbot.event.events.revoke.ChannelMessageRevokeEvent
import io.github.zimoyin.qqbot.event.events.revoke.ChannelPrivateMessageRevokeEvent
import io.github.zimoyin.qqbot.event.events.revoke.MessageRevokeEvent
import io.github.zimoyin.qqbot.event.handler.NoneEventHandler
import io.github.zimoyin.qqbot.net.bean.Payload
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

/**
 *
 * @author : zimo
 * @date : 2023/12/08
 * 事件元类型与事件处理器映射
 * 事件与事件处理器的映射关系
 */
object EventMapping {
    private val mapping = ConcurrentHashMap<String, MateEventMapping>()
    private val history = CopyOnWriteArraySet<Class<*>>()
    private val logger = LocalLogger(EventMapping::class.java)

    init {
        //添加框架预先实现的元事件。如果不添加的话，父事件无法接收到没有开启监听的子事件的信息
        val success: HashSet<String> = HashSet()
        arrayOf(
            Event::class.java,

            //Message
            MessageEvent::class.java,
            PrivateChannelMessageEvent::class.java, //私域监听信息
            //私信事件
            PrivateMessageEvent::class.java,
            UserPrivateMessageEvent::class.java,
            ChannelPrivateMessageEvent::class.java,
            //AT
            AtMessageEvent::class.java,
            ChannelAtMessageEvent::class.java,
            GroupAtMessageEvent::class.java,

            // GUILD_MEMBER
            GuildMemberEvent::class.java,
            GuildMemberAddEvent::class.java,
            GuildMemberDeleteEvent::class.java,
            GuildMemberUpdateEvent::class.java,

            //Channel 操作信息
            ChannelEvent::class.java,
            // Channel -> Guild
            GuildOperate::class.java,
            BotGuildUpdatedEvent::class.java,
            BotJoinedGuildEvent::class.java,
            BotLeftGuildEvent::class.java,
            // Channel -> Channel
            SubChannelEvent::class.java,
            SubChannelCreateEvent::class.java,
            SubChannelDeleteEvent::class.java,
            SubChannelUpdateEvent::class.java,
            // Channel -> Forum
            ForumEvent::class.java,
            // Channel -> Forum -> Audit
            ForumPublishAuditEvent::class.java,
            // Channel -> Forum -> Post
            ForumPostEvent::class.java,
            ForumPostCreateEvent::class.java,
            ForumPostDeleteEvent::class.java,
            // Channel -> Forum -> Reply
            ForumReplyEvent::class.java,
            ForumReplyCreateEvent::class.java,
            ForumReplyDeleteEvent::class.java,
            // Channel -> Forum -> Thread
            ForumThreadEvent::class.java,
            ForumThreadCreateEvent::class.java,
            ForumThreadDeleteEvent::class.java,
            ForumThreadUpdateEvent::class.java,


            //Group
            GroupEvent::class.java,
            GroupMemberUpdateEvent::class.java,
            GroupBotOperationEvent::class.java,
            AddGroupEvent::class.java,
            ExitGroupEvent::class.java,
            CloseGroupBotEvent::class.java,
            OpenGroupBotEvent::class.java,

            //Friend
            FriendEvent::class.java,
            FriendUpdateEvent::class.java,
            AddFriendEvent::class.java,
            DeleteFriendEvent::class.java,
            FriendBotOperationEvent::class.java,
            OpenFriendBotEvent::class.java,
            CloseFriendBotEvent::class.java,

            //MESSAGE_REACTION
            MessagePasteEvent::class.java,
            MessageDeletePasteEvent::class.java,
            MessageAddPasteEvent::class.java,

            //MESSAGE_DELETE
            MessageRevokeEvent::class.java,
            ChannelPrivateMessageRevokeEvent::class.java,
            ChannelMessageRevokeEvent::class.java,

            //Audit
            MessageAuditEvent::class.java,
            MessageStartAuditEvent::class.java,
            MessageAuditPassEvent::class.java,
            MessageAuditRejectEvent::class.java,

            //PlatformEvent
            PlatformEvent::class.java,
            MessageSendPreEvent::class.java,
            MessageSendEvent::class.java,
            ChannelMessageSendPreEvent::class.java,
            ChannelMessageSendEvent::class.java,
            FriendMessageSendEvent::class.java,
            FriendMessageSendPreEvent::class.java,
            GroupMessageSendEvent::class.java,
            GroupMessageSendPreEvent::class.java,
            MessageSendInterceptEvent::class.java,

            //BOT
            BotStatusEvent::class.java,
            BotHelloEvent::class.java,
            BotReconnectNotificationEvent::class.java,
            BotOnlineEvent::class.java,
            BotOfflineEvent::class.java,
            BotResumedEvent::class.java,
            BotReadyEvent::class.java,

            // InteractionEvent
            InteractionEvent::class.java,

            ).forEach { cls ->
            kotlin.runCatching { initAdd(cls) }
                .onFailure {
                    logger.error("EventMapping add error: class $cls", it)
                }
                .onSuccess {
                    success.add(cls.simpleName)
                }
        }
        logger.trace("EventMapping 初始化成功: $success")
    }

    fun add(str: String, mate: MateEventMapping) {
        mapping[str] = mate
    }

    fun <T : Event> add(cls: Class<T>) {
        if (history.contains(cls)) return
        runCatching {
            val initAdd = initAdd(cls)
            logger.debug("EventMapping add mapping: $initAdd ")
        }.onFailure {
            logger.error("EventMapping add error: class $cls", it)
            throw it
        }
    }

    private fun <T : Event> initAdd(cls: Class<T>): MateEventMapping? {
        if (history.contains(cls)) return null
        val isCustomEvent = CustomEvent::class.java.isAssignableFrom(cls)
        val metaTypeAnnotation = cls.getAnnotation(EventAnnotation.EventMetaType::class.java)
        var eventHandlerAnnotation = cls.getAnnotation(EventAnnotation.EventHandler::class.java)

        if (eventHandlerAnnotation == null && !isCustomEvent) logger.warn("该事件上未能声明处理器，将使用父处理器，同时你需要承担使用父处理器带来的事件广播只从该处理器事件进行广播的问题: [$cls]")
        while (eventHandlerAnnotation == null) {
            val superclass = cls.superclass ?: break
            if (superclass == Any::class.java) break
            eventHandlerAnnotation = superclass.getAnnotation(EventAnnotation.EventHandler::class.java)
        }
        // 事件元类型为空，并且不是自定义事件，则抛出异常
        if (metaTypeAnnotation == null && !isCustomEvent)
            throw NullPointerException("There is no EventAnnotation.EventMetaType annotation in $cls")
        // 事件处理器为空，并且不是自定义事件，则抛出异常
        if (eventHandlerAnnotation == null && !isCustomEvent)
            throw NullPointerException("There is no EventAnnotation.EventHandler annotation in $cls")

        // 事件元类型为空，则使用类名作为元类型(针对于自定义事件)
        val metadataType = metaTypeAnnotation?.metadataType ?: cls.name
        // 事件处理器为空，则使用父处理器(针对于自定义事件)
        val eventHandler = eventHandlerAnnotation.eventHandler

        val returnType = eventHandler.java.getMethod("handle", Payload::class.java).returnType
        if ((returnType != cls && eventHandler != NoneEventHandler::class) && !eventHandlerAnnotation.ignore) {
            logger.warn("EventAnnotation.EventHandler 注解中注册的处理器的返回值类型并不是该事件的类型 [$cls] 这将导致无法正确的广播事件，意味着该事件无法被正常广播，而是广播到该处理器处理返回的事件中.")
        }

        mapping[metadataType] = MateEventMapping(
            eventType = metadataType,
            eventCls = cls,
            eventHandler = eventHandler.java
        )
        history.add(cls)
        return mapping[metadataType]
    }

    fun get(str: String): MateEventMapping? = mapping[str]

    @Deprecated("请不要考虑从映射表中清理映射")
    fun remove(str: String): MateEventMapping? = mapping.remove(str)?.apply {
        history.remove(this.eventCls)
    }

    fun clear() {
        mapping.clear()
        history.clear()
    }
}
