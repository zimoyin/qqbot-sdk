package io.github.zimoyin.qqbot.bot.message


import io.github.zimoyin.qqbot.bot.contact.Contact
import io.github.zimoyin.qqbot.bot.message.type.*
import io.github.zimoyin.qqbot.net.bean.message.Message
import io.github.zimoyin.qqbot.net.bean.message.MessageReference
import io.github.zimoyin.qqbot.net.bean.message.send.SendMediaBean
import io.github.zimoyin.qqbot.net.bean.message.send.SendMessageBean
import io.vertx.core.Future
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.util.*
import java.util.stream.Stream

/**
 * 消息链（MessageChain）。
 *
 * 消息链是 QQBot SDK 中表示一条完整消息的核心数据结构。它将一条消息拆解为多个
 * [{@link MessageItem}] 的有序集合，每个 MessageItem 代表消息中的一个独立元素
 * （如文本、图片、表情、@提及、引用等），按发送方的原始顺序排列。
 *
 * **消息链的设计理念：**
 * 在 QQ 的消息模型中，一条消息可能同时包含文本、表情、图片、引用等多种内容。
 * 传统做法是将它们按固定顺序拼接为纯文本，但这样会丢失结构信息和富媒体元数据。
 * MessageChain 通过保留每个元素的独立类型和属性，使开发者可以：
 * - 精确遍历和定位消息中的特定元素（如图片、表情、@提及等）
 * - 直接访问元素的原始数据（如图片 URL、表情 ID、@成员 UID 等）
 * - 按类型过滤和提取消息内容
 *
 * **主动消息 vs 被动消息：**
 * - **主动消息**：发送时未填充 [id]（msg_id）字段的消息。机器人主动向用户/群/频道推送。
 * - **被动消息**：发送时填充了 [id] 字段的消息，该 id 关联到一条用户消息。
 *   QQ 服务器通过此 id 判断用户消息的发送时间，如果超过被动消息回复时效，将拒绝发送。
 *
 * **构造方式：**
 * 推荐使用 [{@link MessageChainBuilder}] 以流式 API 构建消息链：
 * ```
 * val chain = MessageChainBuilder()
 *     .append("你好")
 *     .append(At(userId))
 *     .append(ImageMessage(url))
 *     .build()
 * ```
 * 也可以使用伴生对象中的 [{@link convert}] 方法将服务端下发的 [{@link Message}] Bean
 * 转换为 MessageChain。
 *
 * @param id 消息的唯一标识符（msg_id）。
 *           发送主动消息时可为 null；发送被动回复时必须填充，关联到被回复的用户消息。
 * @param timestamp 消息的创建时间
 * @param editedTimestamp 消息的最后编辑时间
 * @param metaTextContent 消息的原始文本内容（元文本），来自服务端下发的原始 content 字段，
 *                        通常包含未解析的格式标记如 `<emoji:123>`、`<@!uid>` 等
 * @param replyEventID 回复目标消息的事件 ID，用于关联回复链路。为 null 表示不关联特定事件
 * @param internalItems 消息项的有序集合（内部列表），按消息中各元素出现的原始顺序排列
 * @param msgSeq 消息序号，用于保证多条被动回复的顺序性，每次回复自动递增
 *
 * @author : zimo
 * @date : 2023/12/09
 *
 * @see MessageChainBuilder
 * @see MessageItem
 * @see Message
 */
class MessageChain(
    /**
     * 消息的唯一标识符（msg_id）。
     *
     * **主动消息**：发送消息时，未填充 msg_id 字段的消息。
     * **被动消息**：发送消息时，填充了 msg_id 字段的消息。接口使用此 msg_id 拉取用户的消息，
     * 同时判断用户消息的发送时间，如果超过被动消息回复时效，将会不允许发送该消息。
     */
    val id: String? = null,
    /**
     * 消息的创建时间。
     *
     * 记录消息在服务端的生成时刻。对于从服务端接收的消息，此值为服务端下发的时间戳；
     * 对于本地构建的待发送消息，默认为当前时间（[Date]）。
     */
    val timestamp: Date = Date(),
    /**
     * 消息的最后编辑时间。
     *
     * 当用户编辑已发送的消息时，此值会被更新。对于未编辑的消息，通常与 [timestamp] 相同。
     */
    val editedTimestamp: Date = Date(),
    /**
     * 消息的原始文本内容（元文本）。
     *
     * 来自服务端下发的原始 content 字段，保留了消息中的格式标记（如 `<emoji:123>`、
     * `<@!uid>`、`<#channelId>` 等）。在 [convert] 方法中会根据这些标记进行解析，
     * 将对应部分替换为结构化的 [MessageItem]（如 [EmojiMessage]、[At] 等）。
     * 对于本地构建的消息，此值由 [MessageChainBuilder.buildMetaTextContent] 生成。
     */
    val metaTextContent: String? = null,
    /**
     * 回复目标消息的事件 ID。
     *
     * 用于关联回复链路——当此消息是对另一条消息的回复时，此字段记录目标消息的 eventID。
     * 为 null 表示此消息不是对特定事件的回复。
     */
    val replyEventID: String? = null,
    /**
     * 消息项的有序集合（内部列表）。
     *
     * 按消息中各元素出现的原始顺序存储所有 [MessageItem]。
     * 不直接对外暴露，通过 [iterator]、[get]、[toList]、[stream] 等方法访问。
     */
    private val internalItems: ArrayList<MessageItem> = ArrayList(),
    /**
     * 消息序号。
     *
     * 用于保证多条被动回复消息的顺序性。每次通过事件回复消息时，
     * 该序号会自动递增并附加到发送的消息中，确保接收方能按正确顺序展示多条回复。
     */
    private val msgSeq: Int = 1,
) : Serializable, Cloneable, Iterable<MessageItem> {
    companion object {
        /**
         * 将服务端下发的 [{@link Message}] Bean 转换为 MessageChain。
         *
         * 此方法是 SDK 内部解析服务端消息的核心入口。它会：
         * 1. 提取消息的基本属性（id、时间戳、原始文本内容）
         * 2. 将 metaTextContent（原始 content）转换为 [PlainTextMessage] 作为基底
         * 3. 解析并替换格式标记：
         *    - `@everyone` → [AtALL]
         *    - `@频道主` → [AtChannelOwnerAll]
         *    - `@在线成员` → [AtOnlineAll]
         *    - `<@!uid>` → [At]（@提及特定成员）
         *    - `<emoji:id>` → [EmojiMessage]（表情）
         * 4. 处理附件（图片 → [ImageMessage]，其他 → [FileMessage]）
         * 5. 处理消息引用 → [ReferenceMessage]
         * 6. 处理嵌入式内容 → [EmbedMessage]
         * 7. 处理 Ark 消息 → [ArkMessage]
         *
         * @param message0 服务端下发的原始消息 Bean
         * @return 解析后的 MessageChain 实例
         */
        fun convert(message0: Message): MessageChain = MessageChain(
            id = message0.msgID!!,
            timestamp = message0.timestamp ?: Date(),
            editedTimestamp = message0.editedTimestamp ?: Date(),
            metaTextContent = message0.content,
        ).apply {
            internalItems.apply {
                metaTextContent?.let {
                    add(PlainTextMessage(it.replace("\r", "\n")))
                }
                //处理 AT 信息
                // 分割全体成员
                splitAndAdd("@everyone", AtALL("everyone"))
                // 分割频道主
                splitAndAdd("@频道主", AtChannelOwnerAll("频道主"))
                // 分割在线成员
                splitAndAdd("@在线成员", AtOnlineAll("在线成员"))
                // 分割at成员
                message0.mentions?.forEach { user ->
                    splitAndAdd(
                        "<@!${user.uid}>", At(
                            id = user.uid,
                            name = user.username!!,
                            avatar = user.avatar!!,
                            isBot = user.isBot!!,
                            unionOpenID = user.unionOpenID,
                            unionUserAccount = user.unionUserAccount,
                        )
                    )
                }
                //处理表情 <emoji:/id>
                if (metaTextContent != null && metaTextContent.contains("<emoji:\\d+>".toRegex())) {
                    val numbers = Regex("<emoji:(\\d+)>")
                        .findAll(metaTextContent)
                        .map { it.groupValues[1].toInt() }
                        .toMutableList()
                    EmojiType.entries.forEach {
                        if (metaTextContent.contains("<emoji:${it.id}>")) {
                            splitAndAdd("<emoji:${it.id}>", EmojiMessage(it.id.toString(), it))
                            numbers.remove(it.id)
                        }
                    }
                    if (numbers.size > 0) LoggerFactory.getLogger(MessageChain::class.java)
                        .warn("未找到表情ids: $numbers ")
                    numbers.forEach {
                        splitAndAdd("<emoji:${it}>", EmojiMessage(it.toString(), EmojiType.NULL))
                    }
                }
                //处理文件与相册
                message0.attachments?.forEach {
                    if (it.contentType != null && it.contentType.contains("image")) {
                        add(ImageMessage(it.filename, it))
                    } else {
                        add(FileMessage(it.filename, it))
                    }
                }
                //处理 MessageReference
                if (message0.messageReference != null) add(ReferenceMessage(message0.messageReference.messageId!!))
                //处理 embeds
                if (message0.embeds != null) {
                    message0.embeds.forEach {
                        add(EmbedMessage(it))
                    }
                }
                //处理 MessageArk
                if (message0.ark != null) add(ArkMessage(message0.ark))
            }


        }

        /**
         * 创建一个新的 [MessageChainBuilder] 实例。
         *
         * 不预设消息 ID，适用于构建主动消息。
         *
         * @return 新的 MessageChainBuilder 实例
         */
        fun builder(): MessageChainBuilder {
            return MessageChainBuilder()
        }

        /**
         * 基于现有 MessageChain 创建一个新的 [MessageChainBuilder] 实例。
         *
         * 新 Builder 会继承原消息链的 [id]，适用于构建针对某条消息的回复。
         *
         * @param chain 源消息链，其 [id] 将被传递给新 Builder
         * @return 预设了消息 ID 的 MessageChainBuilder 实例
         */
        fun builder(chain: MessageChain): MessageChainBuilder {
            return MessageChainBuilder(chain.id)
        }

        /**
         * 基于指定的消息 ID 创建一个新的 [MessageChainBuilder] 实例。
         *
         * @param id 消息的唯一标识符，将被设置为 Builder 的消息 ID
         * @return 预设了指定消息 ID 的 MessageChainBuilder 实例
         */
        fun builder(id: String): MessageChainBuilder {
            return MessageChainBuilder(id)
        }
    }

    /**
     * 获取消息链中的所有图片元素。
     *
     * @return 过滤后的 [ImageMessage] 列表
     */
    fun getImages(): List<ImageMessage> {
        return internalItems.filterIsInstance<ImageMessage>()
    }

    /**
     * 按精确类型获取消息链中指定类型的消息项。
     *
     * 注意：此方法使用精确的类型匹配（[javaClass]），不匹配子类型。
     * 如需子类型匹配，请使用泛型版本的 [get] 方法。
     *
     * @param cls 要匹配的消息项类型 Class 对象
     * @return 匹配类型的消息项列表
     */
    fun getMessageItems(cls: Class<MessageItem>): List<MessageItem> {
        return internalItems.filter { it.javaClass == cls }
    }

    /**
     * 返回消息链的迭代器，支持 for-each 遍历。
     *
     * @return MessageItem 的迭代器
     */
    override fun iterator(): Iterator<MessageItem> {
        return internalItems.iterator()
    }

    /**
     * 通过索引获取消息链中指定位置的消息项。
     *
     * @param index 从 0 开始的位置索引
     * @return 该位置上的 [MessageItem] 实例
     * @throws IndexOutOfBoundsException 如果索引超出范围
     */
    fun get(index: Int): MessageItem = internalItems[index]

    /**
     * 从消息链中获取指定类型（含子类型）的所有消息项。
     *
     * 使用 Kotlin 的 `reified` 泛型参数和内联函数，支持子类型匹配。
     * 例如 `chain.get<ImageMessage>()` 会返回所有 ImageMessage 及其子类型的实例。
     *
     * @param T 目标消息项类型，必须是 [MessageItem] 的子类型
     * @return 匹配类型（含子类型）的消息项列表
     */
    inline fun <reified T : MessageItem> get(): ArrayList<T> {
        val list = ArrayList<T>()
        toList().forEach {
            if (it is T) list.add(it)
        }
        return list
    }

    /**
     * 获取指定消息项在链中首次出现的位置索引。
     *
     * @param item 要查找的消息项
     * @return 首次出现的索引，不存在则返回 -1
     */
    fun indexOf(item: MessageItem): Int {
        return internalItems.indexOf(item)
    }

    /**
     * 获取指定消息项在链中最后一次出现的位置索引。
     *
     * @param item 要查找的消息项
     * @return 最后一次出现的索引，不存在则返回 -1
     */
    fun lastIndexOf(item: MessageItem): Int {
        return internalItems.lastIndexOf(item)
    }

    /**
     * 获取消息链中消息项的总数。
     *
     * @return 消息项数量
     */
    fun size(): Int {
        return internalItems.size
    }

    /**
     * 判断消息链是否为空（不包含任何消息项）。
     *
     * @return true 表示消息链为空
     */
    fun isEmpty(): Boolean {
        return internalItems.isEmpty()
    }

    /**
     * 判断消息链是否包含指定的消息项（使用 [equals] 比较）。
     *
     * @param item 要查找的消息项
     * @return true 表示包含该消息项
     */
    fun contains(item: MessageItem): Boolean {
        return internalItems.contains(item)
    }

    /**
     * 判断消息链是否包含指定精确类型的消息项。
     *
     * 使用精确类型匹配（[javaClass]），不匹配子类型。
     *
     * @param cls 要查找的类型 Class 对象
     * @return true 表示包含至少一个该类型的消息项
     */
    fun contains(cls: Class<MessageItem>): Boolean {
        return internalItems.any { it.javaClass == cls }
    }

    /**
     * 将消息链中的消息项以数组形式返回。
     *
     * @return 包含所有消息项的数组
     */
    fun toArray(): Array<MessageItem> {
        return internalItems.toTypedArray()
    }

    /**
     * 判断消息链是否包含指定集合中的所有消息项。
     *
     * @param items 待检查的消息项集合
     * @return true 表示包含集合中的所有消息项
     */
    fun containsAll(items: Collection<MessageItem>): Boolean {
        return internalItems.containsAll(items)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MessageChain) return false

        if (id != other.id) return false
        if (internalItems != other.internalItems) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + internalItems.hashCode()
        return result
    }

    /**
     * 返回消息链中消息项的 Java Stream。
     *
     * @return 消息项的串行 Stream
     */
    fun stream(): Stream<MessageItem> {
        return internalItems.stream()
    }

    /**
     * 返回消息链的 Spliterator，支持并行遍历。
     *
     * @return MessageItem 的 Spliterator
     */
    fun spliterator(): Spliterator<MessageItem> {
        return internalItems.spliterator()
    }

    /**
     * 将消息链中的消息项以不可变 List 形式返回。
     *
     * @return 包含所有消息项的只读列表
     */
    fun toList(): List<MessageItem> {
        return internalItems.toList()
    }

    /**
     * 获取消息链的纯文本内容。
     *
     * 遍历所有消息项，调用每个消息项的 [MessageItem.toContent] 方法拼接，
     * 得到一个适合人类阅读的纯文本表示。各消息项类型会自行决定如何转换为文本
     *（例如 [PlainTextMessage] 返回文本本身，[At] 返回被 @ 用户的名称等）。
     *
     * @return 拼接后的纯文本字符串
     */
    fun content(): String {
        val sb = StringBuilder()
        internalItems.forEach {
            sb.append(it.toContent())
        }
        return sb.toString()
    }

    /**
     * 返回消息链的调试用字符串表示。
     *
     * 遍历所有消息项，调用每个消息项的 [MessageItem.toStringType] 方法，
     * 以列表形式展示每个元素的类型和内容摘要。主要用于日志和调试场景。
     *
     * @return 消息链的调试用字符串
     */
    override fun toString(): String {
        val sb = ArrayList<String>()
        internalItems.forEach {
            sb.add(it.toStringType())
        }
        return sb.toString()
    }

    /**
     * 按分隔符拆分纯文本消息项并替换为结构化消息项。
     *
     * 遍历当前消息项列表中的所有 [PlainTextMessage]，将其中包含的 `delimiter`
     * 标记替换为 `wendItem`（如 [AtALL]、[EmojiMessage] 等）。非 PlainTextMessage
     * 类型保持原样不变。
     *
     * 此方法是 [convert] 方法中将服务端元文本中的格式标记（`@everyone`、`<emoji:123>` 等）
     * 转换为结构化 [MessageItem] 的核心实现。
     *
     * @param delimiter 要匹配的分隔符字符串（如 `"@everyone"`、`"<emoji:123>"`）
     * @param wendItem 匹配到分隔符时插入的结构化消息项，为 null 则仅删除分隔符
     */
    private fun ArrayList<MessageItem>.splitAndAdd(
        delimiter: String,
        wendItem: MessageItem? = null,
    ) {
        val temp = ArrayList<MessageItem>()
        forEach { msg ->
            if (msg is PlainTextMessage) {
                msg.content.customSplit(delimiter).forEach {
                    if (it == delimiter) {
                        wendItem?.let { temp.add(wendItem) }
                    } else {
                        temp.add(PlainTextMessage(it))
                    }
                }
            } else {
                temp.add(msg)
            }
        }
        clear()
        addAll(temp)
    }

    /**
     * 按分隔符拆分字符串，保留分隔符作为独立元素。
     *
     * 与标准 [String.split] 不同，此方法不会丢弃分隔符，而是将其保留在结果列表中。
     * 例如 `"A@everyoneB@everyoneC".customSplit("@everyone")` 返回
     * `["A", "@everyone", "B", "@everyone", "C"]`。
     *
     * @param delimiter 分隔符字符串
     * @return 分割后的字符串列表，分隔符作为独立元素保留
     */
    private fun String.customSplit(delimiter: String): List<String> {
        val input = this
        val resultList = mutableListOf<String>()

        var startIndex = 0
        var nextIndex: Int

        do {
            nextIndex = input.indexOf(delimiter, startIndex)

            if (nextIndex != -1) {
                if (startIndex != nextIndex) resultList.add(input.substring(startIndex, nextIndex))
                resultList.add(delimiter)
                startIndex = nextIndex + delimiter.length
            } else {
                resultList.add(input.substring(startIndex))
            }
        } while (nextIndex != -1 && startIndex < input.length)

        return resultList
    }

    /**
     * 通过指定的联系人撤回此消息。
     *
     * 此方法已弃用，推荐直接使用具体的 [{@link Contact}] 实例的撤回方法。
     *
     * @param contact 消息所在的联系人/会话
     * @return 异步结果，true 表示撤回成功；如果 [id] 为 null 则返回 null
     */
    @Deprecated("不推荐使用，建议直接使用具体的 Contact 的方法撤回信息")
    fun recall(contact: Contact): Future<Boolean>? {
        return id?.let { contact.recall(it) }
    }

    /**
     * 将 MessageChain 转换为 [{@link SendMessageBean}]，用于通过 HTTP API 发送消息。
     *
     * 此方法会遍历消息链中的所有消息项，按类型提取对应的发送数据：
     * - [PlainTextMessage]、[EmojiMessage]、[At]、[AtALL]、[AtOnlineAll]、[AtChannelOwnerAll] → content（文本）
     * - [ReferenceMessage] → messageReference（消息引用）
     * - [ImageMessage] → 图片文件/URL/字节数组
     * - [AudioMessage] → 音频文件/URL/字节数组
     * - [VideoMessage] → 视频文件/URL/字节数组
     * - [ArkMessage] → ark 数据
     * - [EmbedMessage] → embed 数据
     * - [MarkdownMessage] → markdown 数据（若为服务端主动发送消息则不设 id）
     * - [KeyboardMessage] → keyboard/button 数据
     *
     * **注意**：对于同类型的多个消息项（如多张图片），仅取最后一个。
     *
     * @return 用于发送的 [SendMessageBean] 实例
     * @see SendMessageBean
     */
    fun convertSendMessageBean(): SendMessageBean {
        val reference = internalItems.filterIsInstance<ReferenceMessage>().lastOrNull()?.let { MessageReference(it.id) }
        val sb = StringBuilder()
        internalItems.forEach {
            if (it is PlainTextMessage) sb.append(it.toMetaContent())
            if (it is EmojiMessage) sb.append(it.toMetaContent())
            if (it is At) sb.append(it.toMetaContent())
            if (it is AtALL) sb.append(it.toMetaContent())
            if (it is AtOnlineAll) sb.append(it.toMetaContent())
            if (it is AtChannelOwnerAll) sb.append(it.toMetaContent())
        }
        val image = internalItems.filterIsInstance<ImageMessage>().lastOrNull()
        val imageURL = image?.attachment?.getURL()
        val imageFile = image?.localFile
        val imageFileBytes = image?.localFileBytes

        val audio = internalItems.filterIsInstance<AudioMessage>().lastOrNull()
        val audioURL = audio?.attachment?.getURL()
        val audioFile = audio?.localFile
        val audioFileByte = audio?.localFileBytes

        val video = internalItems.filterIsInstance<VideoMessage>().lastOrNull()
        val videoURL = video?.attachment?.getURL()
        val videoFile = video?.localFile
        val videoFileBytes = video?.localFileBytes

        val ark = internalItems.filterIsInstance<ArkMessage>().lastOrNull()?.ark
        val embed = internalItems.filterIsInstance<EmbedMessage>().lastOrNull()?.embed
        val md = internalItems.filterIsInstance<MarkdownMessage>().lastOrNull()?.markdown
        val kb = internalItems.filterIsInstance<KeyboardMessage>().lastOrNull()
        val button = internalItems.filterIsInstance<KeyboardMessage>().lastOrNull()

        val fileType = when {
            image != null -> SendMediaBean.FILE_TYPE_IMAGE
            audio != null -> SendMediaBean.FILE_TYPE_AUDIO
            video != null -> SendMediaBean.FILE_TYPE_VIDEO
            else -> SendMediaBean.FILE_TYPE_IMAGE
        }

        return SendMessageBean(
            id = if (md != null && md._isSrvSendMsg) null else this.id,
            messageReference = reference,
            content = if (sb.isEmpty()) null else sb.toString(),
            button = button,
            ark = ark,
            embed = embed,
            markdown = md,
            keyboard = kb,
            file = imageFile ?: audioFile ?: videoFile,
            fileBytes = imageFileBytes ?: audioFileByte ?: videoFileBytes,
            fileUri = imageURL ?: audioURL ?: videoURL,
            fileType = fileType,
            eventID = replyEventID,
            msgSeq = msgSeq
        )
    }

}

