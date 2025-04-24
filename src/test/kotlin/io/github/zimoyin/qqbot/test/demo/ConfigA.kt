import io.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import io.github.zimoyin.qqbot.bot.Bot
import io.github.zimoyin.qqbot.event.events.message.at.ChannelAtMessageEvent
import io.github.zimoyin.qqbot.event.supporter.EventMapping
import io.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import io.github.zimoyin.qqbot.net.Token
import io.github.zimoyin.qqbot.net.bean.Payload
import io.github.zimoyin.qqbot.net.http.api.HttpAPIClient.logger
import io.github.zimoyin.qqbot.utils.JSON
import org.slf4j.LoggerFactory
import java.io.File


fun loadTokenFromFile(): Token? {
  val filePath = "./token_file_2" // 替换为你的 token 文件路径
//  val filePath = "./token_file" // 替换为你的 token 文件路径

  return try {
    val lines = File(filePath).readLines()
    if (lines.size >= 3) {
      Token(lines[0], lines[1], lines[2])
    } else {
      println("Token file format is incorrect")
      null
    }
  } catch (e: Exception) {
    println("Error loading token from file: ${e.message}")
    null
  }
}

val token = loadTokenFromFile()!!

fun Any.log() {
    LoggerFactory.getLogger("Test").info(this.toString())
}

fun openDebug() {
    System.setProperty("org.slf4j.simpleLogger.log.io.netty", "info")
//    System.setProperty("org.slf4j.simpleLogger.log.io.vertx", "info")
    System.setProperty("org.slf4j.simpleLogger.log.io.vertx.core.logging.LoggerFactory", "info")
    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug")


    // 获取当前时间
//    val now: Date = Date()
//    val formatter = SimpleDateFormat("yyyyMMdd_HHmmss")
//    val formattedTime = formatter.format(now)
//    val logFilePath = "./log/$formattedTime.log"
//    File(logFilePath).parentFile.mkdirs()
//    System.setProperty("org.slf4j.simpleLogger.logFile", File(logFilePath).absolutePath)


}


fun main() {
    openDebug()

    GlobalEventBus.onEvent<ChannelAtMessageEvent> {
        println(it.messageChain.content())
    }

//    val json = File("./mock/event/DIRECT_MESSAGE_CREATE.json").readText()
//    val json = File("./mock/event/AT_MESSAGE_CREATE.json").readText()
    val json = File("./mock/event/t.json").readText()

    val payload = JSON.toObject<Payload>(json).apply {
        metadata = json
        appID = Bot.INSTANCE.createBot(token).config.token.appID
    }


    payload.eventType?.apply {// 获取元事件类型
        EventMapping.get(this)?.apply {// 获取注册的元事件
            eventHandler.getDeclaredConstructor().newInstance().apply { // 获取该事件类型的处理器
                try {
                    GlobalEventBus.broadcastAuto(handle(payload)) //广播事件
                } catch (e: Exception) {
                    logger.error("广播事件失败: ${payload.eventType} -> ${payload.metadata}", e)
                }
            }
        } ?: logger.warn("未注册的事件类型: ${payload.eventType} -> ${payload.metadata}")
    } ?: logger.debug("服务器推送的消息为空(ws send(0)): ${payload.metadata}", )

    GLOBAL_VERTX_INSTANCE.close()
}
