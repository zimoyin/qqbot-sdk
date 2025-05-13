package io.github.zimoyin.qqbot.bot.contact.channel

import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.Channel
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.net.bean.ChannelBean
import io.github.zimoyin.qqbot.net.bean.SendMessageResultBean
import io.github.zimoyin.qqbot.net.bean.message.Message
import io.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import io.github.zimoyin.qqbot.net.http.api.channel.sendChannelMessageAsync
import io.github.zimoyin.qqbot.net.http.api.channel.sendChannelPrivateMessageAsync
import io.vertx.core.Future

data class ChannelImpl(
    override val id: String,
    override val guildID: String,
    override val channelID: String?,
    override val currentID: String,
    override val botInfo: BotInfo,
) : Channel {
    companion object {
        fun convert(info: BotInfo, message: Message): ChannelImpl = ChannelImpl(
            id = message.guildID!!,
            guildID = message.guildID,
            channelID = message.channelID,
            currentID = message.srcGuildID ?: message.channelID ?: message.guildID,
            botInfo = info
        )

        fun convert(info: BotInfo, guildID: String, channelID: String?, srcGuildID: String?): ChannelImpl = ChannelImpl(
            id = guildID,
            guildID = guildID,
            channelID = channelID,
            currentID = srcGuildID ?: channelID ?: guildID,
            botInfo = info
        )

        fun convert(info: BotInfo, channelBean: ChannelBean): ChannelImpl = ChannelImpl(
            id = channelBean.guildID,
            guildID = channelBean.guildID,
            channelID = channelBean.channelID,
            currentID = channelBean.channelID,
            botInfo = info
        )
    }

    @Deprecated("Official ban on active sending")
    override fun send(message: MessageChain): Future<SendMessageResultBean> {
        return if (currentID == channelID) HttpAPIClient.sendChannelMessageAsync(this, message)
        else HttpAPIClient.sendChannelPrivateMessageAsync(this, message)
    }
}
