package io.github.zimoyin.qqbot.event.events.message

import io.github.zimoyin.qqbot.annotation.EventAnnotation
import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.contact.ChannelPrivateUser
import io.github.zimoyin.qqbot.bot.message.EmojiType
import io.github.zimoyin.qqbot.event.events.channel.ChannelEvent
import io.github.zimoyin.qqbot.event.handler.message.MessageHandler
import io.github.zimoyin.qqbot.net.bean.User
import io.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import io.github.zimoyin.qqbot.net.http.api.channel.addEmoji
import io.github.zimoyin.qqbot.net.http.api.channel.deleteEmoji
import io.github.zimoyin.qqbot.net.http.api.channel.getEmojiUserList
import io.github.zimoyin.qqbot.net.http.api.channel.recallChannelMessage
import io.vertx.core.Future

/**
 * 频道/子频道消息事件接口，继承自 {@link MessageEvent} 和 {@link ChannelEvent}。
 *
 * 针对频道场景（文字子频道等），在基础消息事件之上扩展了频道特有的操作能力，
 * 包括表情反应管理（添加/删除表情）、获取表情反应用户列表、以及撤回消息等功能。
 *
 * 此接口同时提供了实例方法和伴生对象的静态方法，方便在不同场景下调用。
 *
 * @author : zimo
 * @date : 2023/12/20
 */
@EventAnnotation.EventMetaType("Not_MetaType_ChannelMessageEvent")
@EventAnnotation.EventHandler(MessageHandler::class, true)
interface ChannelMessageEvent : MessageEvent, ChannelEvent {
    /**
     * 会话窗口，具体为当前频道（{@link Channel}）。
     *
     * 重写了父接口 {@link MessageEvent#windows} 的类型，从通用的 {@code Contact} 收窄为 {@code Channel}，
     * 以便直接使用频道相关的属性和方法。
     */
    override val windows: Channel

    /**
     * 消息发送者，具体为频道私密用户（{@link ChannelPrivateUser}）。
     *
     * 重写了父接口 {@link MessageEvent#sender} 的类型，收窄为 {@code ChannelPrivateUser}，
     * 方便访问频道用户特有的信息（如频道内的昵称、角色等）。
     */
    override val sender: ChannelPrivateUser

    /**
     * 在当前消息上添加表情反应。
     *
     * 向当前事件关联的消息张贴一个指定的表情符号，类似于 QQ 或 Discord 中的"贴表情"功能。
     * 此API尚未经过充分测试，使用时需注意可能存在的稳定性问题。
     *
     * @param emojiType 要添加的表情类型（{@link EmojiType}）
     * @return 异步结果，操作成功时返回 {@code true}
     */
    @OptIn(UntestedApi::class)
    fun addEmoji(emojiType: EmojiType): Future<Boolean> {
        return HttpAPIClient.addEmoji(channel, msgID, emojiType)
    }

    /**
     * 在指定消息上添加表情反应。
     *
     * 与 {@link #addEmoji(EmojiType)} 的区别在于可以指定任意的消息ID，
     * 而不是仅限当前事件关联的消息。
     *
     * @param emojiType 要添加的表情类型（{@link EmojiType}）
     * @param messageID 目标消息的ID
     * @return 异步结果，操作成功时返回 {@code true}
     */
    @OptIn(UntestedApi::class)
    fun addEmoji(emojiType: EmojiType, messageID: String): Future<Boolean> {
        return HttpAPIClient.addEmoji(channel, messageID, emojiType)
    }

    /**
     * 从当前消息上移除表情反应。
     *
     * 删除之前在当前消息上张贴的表情符号。
     * 此API尚未经过充分测试，使用时需注意可能存在的稳定性问题。
     *
     * @param emojiType 要移除的表情类型（{@link EmojiType}）
     * @return 异步结果，操作成功时返回 {@code true}
     */
    @OptIn(UntestedApi::class)
    fun removeEmoji(emojiType: EmojiType): Future<Boolean> {
        return HttpAPIClient.deleteEmoji(channel, msgID, emojiType)
    }

    /**
     * 从指定消息上移除表情反应。
     *
     * 与 {@link #removeEmoji(EmojiType)} 的区别在于可以指定任意的消息ID，
     * 而不是仅限当前事件关联的消息。
     *
     * @param emojiType 要移除的表情类型（{@link EmojiType}）
     * @param messageID 目标消息的ID
     * @return 异步结果，操作成功时返回 {@code true}
     */
    @OptIn(UntestedApi::class)
    fun removeEmoji(emojiType: EmojiType, messageID: String): Future<Boolean> {
        return HttpAPIClient.deleteEmoji(channel, messageID, emojiType)
    }

    /**
     * 获取当前消息上对指定表情做出反应的用户列表。
     *
     * 查询有哪些用户对当前消息张贴了指定的表情符号。
     * 此API标记为 {@code @UntestedApi}，尚未经过充分测试验证。
     *
     * @param emojiType 要查询的表情类型（{@link EmojiType}）
     * @return 异步结果，返回对该表情做出反应的用户列表
     */
    @UntestedApi
    fun getEmojiList(emojiType: EmojiType): Future<List<User>> {
        return HttpAPIClient.getEmojiUserList(channel, msgID, emojiType)
    }

    /**
     * 获取指定消息上对指定表情做出反应的用户列表。
     *
     * 与 {@link #getEmojiList(EmojiType)} 的区别在于可以指定任意的消息ID。
     *
     * @param emojiType 要查询的表情类型（{@link EmojiType}）
     * @param messageID 目标消息的ID，默认使用当前消息的ID
     * @return 异步结果，返回对该表情做出反应的用户列表
     */
    @UntestedApi
    fun getEmojiList(emojiType: EmojiType, messageID: String = msgID): Future<List<User>> {
        return HttpAPIClient.getEmojiUserList(channel, messageID, emojiType)
    }

    /**
     * 撤回当前消息。
     *
     * 在当前频道中撤回（删除）此事件关联的消息。撤回后，频道成员将无法再看到该消息。
     * 此API尚未经过充分测试，使用时需注意可能存在的稳定性问题。
     *
     * @param hidetip 是否隐藏撤回提示（小灰条）。{@code true} 表示撤回后不显示"某某撤回了一条消息"的提示，
     *                {@code false} 表示显示提示
     * @return 异步结果，操作成功时返回 {@code true}
     */
    @OptIn(UntestedApi::class)
    fun recall(hidetip: Boolean): Future<Boolean> {
        return HttpAPIClient.recallChannelMessage(channel, msgID, hidetip)
    }

    /**
     * 撤回指定消息。
     *
     * 与 {@link #recall(Boolean)} 的区别在于可以指定任意的消息ID。
     *
     * @param hidetip 是否隐藏撤回提示（小灰条）。{@code true} 表示不显示撤回提示，{@code false} 表示显示提示
     * @param messageID 要撤回的消息ID
     * @return 异步结果，操作成功时返回 {@code true}
     */
    @OptIn(UntestedApi::class)
    fun recall(hidetip: Boolean, messageID: String): Future<Boolean> {
        return HttpAPIClient.recallChannelMessage(channel, messageID, hidetip)
    }

    /**
     * 撤回当前消息（默认显示撤回提示）。
     *
     * 这是 {@link #recall(Boolean)} 的便捷重载，等同于调用 {@code recall(false)}，
     * 撤回后会显示撤回提示小灰条。
     *
     * @return 异步结果，操作成功时返回 {@code true}
     */
    @OptIn(UntestedApi::class)
    fun recall(): Future<Boolean> {
        return recall(false)
    }

    companion object {
        /**
         * 静态方法：在指定频道消息上添加表情反应。
         *
         * 当不需要通过事件实例调用时，可以使用此静态方法直接操作。
         *
         * @param channel 目标频道
         * @param msgID 目标消息的ID
         * @param emojiType 要添加的表情类型（{@link EmojiType}）
         * @return 异步结果，操作成功时返回 {@code true}
         */
        @OptIn(UntestedApi::class)
        @JvmStatic
        fun addEmoji(channel: Channel, msgID: String, emojiType: EmojiType): Future<Boolean> {
            return HttpAPIClient.addEmoji(channel, msgID, emojiType)
        }

        /**
         * 静态方法：在指定频道消息上移除表情反应。
         *
         * 当不需要通过事件实例调用时，可以使用此静态方法直接操作。
         *
         * @param channel 目标频道
         * @param msgID 目标消息的ID
         * @param emojiType 要移除的表情类型（{@link EmojiType}）
         * @return 异步结果，操作成功时返回 {@code true}
         */
        @OptIn(UntestedApi::class)
        @JvmStatic
        fun deleteEmoji(channel: Channel, msgID: String, emojiType: EmojiType): Future<Boolean> {
            return HttpAPIClient.deleteEmoji(channel, msgID, emojiType)
        }
    }
}
