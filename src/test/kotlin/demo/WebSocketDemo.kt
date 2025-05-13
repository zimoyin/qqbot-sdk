package demo

import io.github.zimoyin.qqbot.Config.GLOBAL_VERTX_INSTANCE
import io.github.zimoyin.qqbot.LocalLogger.Companion.getLogger
import io.github.zimoyin.qqbot.bot.Bot.INSTANCE.createBot
import io.github.zimoyin.qqbot.bot.BotConfigBuilder
import io.github.zimoyin.qqbot.bot.onEvent
import io.github.zimoyin.qqbot.event.events.Event
import io.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import io.github.zimoyin.qqbot.net.Intents
import io.github.zimoyin.qqbot.net.Token.Companion.create
import io.github.zimoyin.qqbot.net.http.TencentOpenApiHttpClient.isSandBox
import io.vertx.core.Handler
import io.vertx.core.http.WebSocket
import java.util.function.Consumer

/**
 *
 * @author : zimo
 * &#064;date : 2025/04/24
 */
fun main() {

    var logger = getLogger()
    var appId = "xxx"
    var appSecret = "xxx"
    var tokenStr = "xxx"
    var useTokenVersion = 2

    // 设置沙盒环境
    isSandBox = true

    // 获取 Token
    val token = create(
        appId,
        tokenStr,
        appSecret
    ).putVersion(useTokenVersion)


    // 监听全局事件（该事件在所有的 Vertx 上传播）
    GlobalEventBus.onEvent<Event> {
        logger.info("收到事件：" + it.toString())
    }


    // 创建Bot
    val bot = createBot(Consumer { config: BotConfigBuilder? ->
        config!!.setToken(token)
        config.setIntents(Intents.Presets.PUBLIC_GROUP_INTENTS)
    })


    // 监听在当前机器人总线上传播的事件
    // 第一个参数可以省略，设置为 true 后则事件处理逻辑运行在工作线程
    bot.onEvent<Event>(false) {
        logger.info("收到事件：" + it.toString())
    }


    // 登录
    bot.login().onSuccess(Handler { ws: WebSocket? ->
        logger.info("登录成功")
    }).onFailure(Handler { err: Throwable? ->
        logger.error("登录失败", err)
        bot.close()
        GLOBAL_VERTX_INSTANCE.close()
    })
}
