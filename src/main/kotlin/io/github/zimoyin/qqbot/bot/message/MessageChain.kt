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
 *
 * @author : zimo
 * @date : 2023/12/09
 *
 */
class MessageChain(
    /**
     * 信息ID，如果需要构造信息发送的话请注意以下内容:
     * 主动消息：发送消息时，未填充 msg_id 字段的消息。
     * 被动消息：发送消息时，填充了 msg_id 字段的消息。接口使用此 msg_id 拉取用户的消息，同时判断用户消息的发送时间，如果超过被动消息回复时效，将会不允许发送该消息。
     */
    val id: String? = null,
    /**
     * 当前信息创建的时间
     */
    val timestamp: Date = Date(),
    val editedTimestamp: Date = Date(),
    val metaTextContent: String? = null,
    val replyEventID: String? = null,
    private val internalItems: ArrayList<MessageItem> = ArrayList(),
    private val msgSeq: Int = 1,
) : Serializable, Cloneable, Iterable<MessageItem> {
    companion object {
        /**
         * 将bean 转为 信息集
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

        fun builder(): MessageChainBuilder {
            return MessageChainBuilder()
        }

        fun builder(chain: MessageChain): MessageChainBuilder {
            return MessageChainBuilder(chain.id)
        }

        fun builder(id: String): MessageChainBuilder {
            return MessageChainBuilder(id)
        }
    }

    fun getImages(): List<ImageMessage> {
        return internalItems.filterIsInstance<ImageMessage>()
    }

    fun getMessageItems(cls: Class<MessageItem>): List<MessageItem> {
        return internalItems.filter { it.javaClass == cls }
    }

    override fun iterator(): Iterator<MessageItem> {
        return internalItems.iterator()
    }

    /**
     * 通过索引获取信息实例
     */
    fun get(index: Int): MessageItem = internalItems[index]

    /**
     * 从信息链中获取指定类型的信息类型集合
     */
    inline fun <reified T : MessageItem> get(): ArrayList<T> {
        val list = ArrayList<T>()
        toList().forEach {
            if (it is T) list.add(it)
        }
        return list
    }


    fun indexOf(item: MessageItem): Int {
        return internalItems.indexOf(item)
    }

    fun lastIndexOf(item: MessageItem): Int {
        return internalItems.lastIndexOf(item)
    }

    fun size(): Int {
        return internalItems.size
    }

    fun isEmpty(): Boolean {
        return internalItems.isEmpty()
    }

    fun contains(item: MessageItem): Boolean {
        return internalItems.contains(item)
    }

    fun contains(cls: Class<MessageItem>): Boolean {
        return internalItems.any { it.javaClass == cls }
    }

    fun toArray(): Array<MessageItem> {
        return internalItems.toTypedArray()
    }

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

    fun stream(): Stream<MessageItem> {
        return internalItems.stream()
    }

    fun spliterator(): Spliterator<MessageItem> {
        return internalItems.spliterator()
    }

    fun toList(): List<MessageItem> {
        return internalItems.toList()
    }

    /**
     * 获取文本消息内容
     */
    fun content(): String {
        val sb = StringBuilder()
        internalItems.forEach {
            sb.append(it.toContent())
        }
        return sb.toString()
    }

    override fun toString(): String {
        val sb = ArrayList<String>()
        internalItems.forEach {
            sb.add(it.toStringType())
        }
        return sb.toString()
    }


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
     * 信息撤回
     */
    @Deprecated("不推荐使用，建议直接使用具体的 Contact 的方法撤回信息")
    fun recall(contact: Contact): Future<Boolean>? {
        return id?.let { contact.recall(it) }
    }

    /**
     * 将 MessageChain 转换为 ChannelMessage。用于发送消息
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

