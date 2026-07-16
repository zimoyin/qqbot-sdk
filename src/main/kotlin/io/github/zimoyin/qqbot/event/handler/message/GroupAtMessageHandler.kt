package io.github.zimoyin.qqbot.event.handler.message

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.contact.GroupImpl
import io.github.zimoyin.qqbot.bot.contact.Sender
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.event.events.message.at.GroupAtMessageEvent
import io.github.zimoyin.qqbot.event.supporter.AbsEventHandler
import io.github.zimoyin.qqbot.net.bean.Payload
import io.github.zimoyin.qqbot.net.bean.message.Message
import io.github.zimoyin.qqbot.utils.JSON
import io.github.zimoyin.qqbot.utils.ex.toJsonObject
import org.slf4j.LoggerFactory

/**
 *
 * @author : zimo
 * @date : 2023/12/11
 * 频道私信事件
 */
class GroupAtMessageHandler : AbsEventHandler<GroupAtMessageEvent>() {
    private val logger = LoggerFactory.getLogger(GroupAtMessageHandler::class.java)

    override fun handle(payload: Payload): GroupAtMessageEvent {
        // 注意：部分机器人服务器会下发 username，部分不会。
        // 如果 username 不为空则直接使用；如果为空则回退到 id 作为 username
        val jsonString = payload.eventContent.toString()
        val mapper = ObjectMapper()
        val jsonNode = mapper.readTree(jsonString)

        var jsonObject = jsonNode.toJsonObject()
        jsonObject = jsonObject.apply {
            val value = getJsonObject("author")?.apply {
                if (getString("username") == null) put("username", getString("id"))
            }
            put("author", value)
        }

        val message = JSON.toObject<Message>(jsonObject)
        val msgID = message.msgID!!
        val messageChain = MessageChain.convert(message)
        val botInfo = BotInfo.create(payload.appID!!)
        val sender = Sender.convert(botInfo, message)
        val group = GroupImpl.convert(botInfo, message)

        return GroupAtMessageEvent(
            metadata = payload.metadata,
            msgID = msgID,
            windows = group,
            messageChain = messageChain,
            sender = sender,
            botInfo = botInfo,
            groupID = group.id,
            timestamp = message.timestamp!!,
            opMemberOpenid = sender.id,
            eventID = payload.eventID?:""
        )
    }
}
