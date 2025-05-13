package io.github.zimoyin.qqbot.bot.contact

import io.github.zimoyin.qqbot.bot.BotInfo
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.net.bean.SendMessageResultBean
import io.vertx.core.Future
import java.io.Serializable

/**
 *
 * @author : zimo
 * @date : 2023/12/09
 * 联系人
 */
interface Contact : Serializable {
    /**
     * 联系人ID
     */
    val id: String

    val botInfo: BotInfo

    /**
     * 主动发送信息
     *
     * @频道
     * 主动消息没有ID
     * 被动消息有ID
     * @title 群里/单聊
     * 主动消息审核，可以通过 Intents 中审核事件 MESSAGE_AUDIT 返回 MessageAudited 对象获取结果。
     * @link https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/template/model.html#messageaudited
     * 发送信息到某个会话/联系人中，注意机器人在主动发送信息上具有限制。如果需要被动回复请在 MessageChain 中设置信息ID
     *
     * 注意该方法可以发送主动，被动，引用等信息，具体类型请根据协议自定构建信息体. 只有 MessageEvent 类中 信息回复部分 会提供其他方式的直接实现
     */
    @Deprecated("Official ban on active sending")
    fun send(message: MessageChain): Future<SendMessageResultBean>

    /**
     * 撤回消息
     * @param messageID 消息ID
     */
    fun recall(messageID: String): Future<Boolean> {
        TODO("This method has not been implemented")
    }
}
