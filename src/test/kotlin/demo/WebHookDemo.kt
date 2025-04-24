package demo

import io.github.zimoyin.qqbot.Config
import io.github.zimoyin.qqbot.LocalLogger
import io.github.zimoyin.qqbot.bot.Bot
import io.github.zimoyin.qqbot.net.Token
import io.github.zimoyin.qqbot.net.webhook.WebHookConfig.Companion.builder

/**
 *
 * @author : zimo
 * &#064;date : 2025/04/24
 */
fun main() {
    val logger = LocalLogger.getLogger()
    // 创建 token
    val token = Token.create("appid", "token", "appSecret")
    // 创建 bot
    val bot = Bot.createBot(token)
    // 创建 webhook
    val webHookConfig = builder()
        .sslPath("./127.0.0.1")
        .isSSL(true)
        .port(8080)
        .password("")
        .enableWebSocketForwarding(true)
        .enableWebSocketForwardingLoginVerify(true)
        .build()

    bot.start(webHookConfig).onSuccess { http ->
        logger.info("登录成功: {}", http.webHttpServer.actualPort());
    }.onFailure { err ->
        logger.error("登录失败", err);
        bot.close();
        Config.GLOBAL_VERTX_INSTANCE.close();
    }
}
