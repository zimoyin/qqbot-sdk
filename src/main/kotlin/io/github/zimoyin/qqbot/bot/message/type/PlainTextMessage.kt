package io.github.zimoyin.qqbot.bot.message.type

class PlainTextMessage(val content: String) : MessageItem {
    override fun toContent(): String {
        return content
    }

    override fun toStringType(): String {
        return "[PlainText:${content.replace("\n", "\\n")}]"
    }

    override fun toMetaContent(): String {
        return content.replace("<", "&lt;").replace(">", "&gt;")
    }
}
