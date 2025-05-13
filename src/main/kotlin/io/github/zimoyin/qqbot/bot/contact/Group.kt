package io.github.zimoyin.qqbot.bot.contact

import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.net.bean.SendMessageResultBean
import io.github.zimoyin.qqbot.net.bean.message.Message
import io.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import io.github.zimoyin.qqbot.net.http.api.group.recallGroupMessage
import io.github.zimoyin.qqbot.net.http.api.group.sendGroupMessage
import io.vertx.core.Future

/**
 *
 * @author : zimo
 * @date : 2023/12/11
 */
interface Group : Contact {
    override val id: String
    override val botInfo: BotInfo

    @OptIn(UntestedApi::class)
    override fun recall(messageID: String): Future<Boolean> {
        return HttpAPIClient.recallGroupMessage(this, messageID)
    }

    @Deprecated("Official ban on active sending")
    override fun send(message: MessageChain): Future<SendMessageResultBean> {
        return HttpAPIClient.sendGroupMessage(this, message)
    }
}

data class GroupImpl(
    override val id: String,
    override val botInfo: BotInfo,
) : Group {

    companion object {
        fun convert(botInfo: BotInfo, message: Message) = GroupImpl(
            botInfo = botInfo,
            id = message.groupID!!
        )

        fun convert(botInfo: BotInfo, groupID: String) = GroupImpl(
            botInfo = botInfo,
            id = groupID
        )
    }
}
