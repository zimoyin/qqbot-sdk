package io.github.zimoyin.qqbot.net.websocket

import io.github.zimoyin.qqbot.LocalLogger
import io.github.zimoyin.qqbot.bot.Bot
import io.github.zimoyin.qqbot.bot.BotSection
import io.github.zimoyin.qqbot.event.events.platform.bot.BotOfflineEvent
import io.github.zimoyin.qqbot.event.supporter.GlobalEventBus
import io.github.zimoyin.qqbot.exception.WebSocketReconnectException
import io.github.zimoyin.qqbot.net.Intents
import io.github.zimoyin.qqbot.net.http.TencentOpenApiHttpClient
import io.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import io.github.zimoyin.qqbot.net.http.api.gatewayV2Async
import io.github.zimoyin.qqbot.net.websocket.handler.PayloadCmdHandler
import io.github.zimoyin.qqbot.utils.ex.await
import io.github.zimoyin.qqbot.utils.ex.executeBlockingKt
import io.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.http.HttpClosedException
import io.vertx.core.http.WebSocket
import io.vertx.core.http.WebSocketClient
import io.vertx.core.http.WebSocketClientOptions
import io.vertx.kotlin.coroutines.CoroutineVerticle
import java.net.InetAddress
import java.net.SocketException
import java.net.URI

/**
 *
 * @author : zimo
 * @project : qqbot_gf
 * @date : 2023/12/06/18:30
 * @description ：
 */
class WebsocketClient(
    private val bot: Bot,
    private val isVerifyHost: Boolean = true,
    private val promise0: Promise<WebSocket>? = null
) : CoroutineVerticle() {
    private val logger = LocalLogger(WebsocketClient::class.java)
    private val promise: Promise<WebSocket> = promise0 ?: bot.context.getValue<Promise<WebSocket>>("internal.promise")
    private var reconnectTime: Long = 1 * 1000
    private var gatewayURL: String? = null // 网关接入点，通常为 TencentOpenApiHttpClient 中 定义的远程主机 host 与网关 path 组成
    private var client: WebSocketClient? = null
    private var headerCycle: Long = 10 * 1000
    private val handlerKey = "internal.handler"
    private val headerCycleKey = "internal.headerCycle"
    private val isAbnormalCardiacArrestKey = "internal.isAbnormalCardiacArrest"
    private val throwableKey = "internal.throwable"
    private var WS: WebSocket? = null
    private var payloadCmdHandler: PayloadCmdHandler? = null


    override suspend fun start() {
        val useSSL = bot.context["client_use_ssl"]?:true
        val headerCycleValue: Long? = bot.context[headerCycleKey]
        if (headerCycleValue != null) {
            headerCycle = headerCycleValue
        }

        //配置 ws
        val options = WebSocketClientOptions()
            .setConnectTimeout(6000)
            .setSsl(useSSL)
            .setTrustAll(true)
            .setVerifyHost(isVerifyHost)
            .apply {
                //如果心跳在 心跳周期 + 30s 内没有发送出去就抛出异常
                val value: Boolean = bot.context[isAbnormalCardiacArrestKey] ?: return@apply
                if (value) this.setWriteIdleTimeout((headerCycle / 1000 + 30).toInt())
            }
        client = vertx.createWebSocketClient(options)

        logger.info("WebSocketClient[${client.hashCode()}] 配置完成 -> 绑定 Bot AppID[${bot.config.token.appID}]")
        logger.debug("心跳周期为: ${headerCycle / 1000.0}s")

        //准备 服务器地址
        val gatewayURLByContent = if (TencentOpenApiHttpClient.isUseCustomHost){
            TencentOpenApiHttpClient.webSocketForwardingAddress
        }else{
            bot.context.getString("gatewayURL")
        }

        if (gatewayURLByContent != null) {
            //为了方便，在没有分片的情况下使用默认的硬编码的URL。但是可能回出现BUG，因为这是一个不再维护的使用
            if (bot.config.shards != BotSection()) logger.warn("自定义WSS接入点的分片非默认值")
            if (!TencentOpenApiHttpClient.isSandBox) logger.warn("当前环境不是沙盒环境，请将环境设置为沙盒环境 > DefaultHttpClient.isSandBox = true")
            if (TencentOpenApiHttpClient.isUseCustomHost) logger.warn("你正在使用自定义WSS接入点: $gatewayURLByContent")
            bot.context["shards"] = 1
            gatewayURL = gatewayURLByContent
        } else {
            val gateway = HttpAPIClient.gatewayV2Async(bot.config.token).await()
            gateway.getInteger("code")?.apply {
                check(gateway.getString("message") != "Token错误") { "cannot get the login point, reason is Token / AppID / Secret error" }
                check(gateway.getString("message") != "接口访问源IP不在白名单") {
                    if (bot.config.intents == Intents.Presets.DEFAULT.code) "cannot get the login point, reason is intents not set"
                    else "cannot get the login point, reason is IP access source is not in the white list, please add the current server IP to the IP white list of QQ development platform. Or set the environment to sandbox environment (test environment) > TencentOpenApiHttpClient.isSandBox = true"
                }
                throw IllegalStateException("cannot get the login point, reason is: " + gateway.getString("message"))
            }
            bot.context["shards"] = gateway.getInteger("shards") ?: 1 //推荐分片数
            //获取 ws 地址
            gatewayURL = gateway.getString("url")
            gatewayURL ?: throw NullPointerException("not found the WSS access point")
        }
        bot.context["internal.headerCycle"] = bot.context.getOrDefault<Long>("internal.headerCycle", headerCycle)
        connect(client!!, bot.config.reconnect, bot.config.retry)
    }

    /**
     * @param reconnect 是否允许重连
     * @param retry 自动重连次数
     */
    private fun connect(client: WebSocketClient, reconnect: Boolean = false, retry: Int = -1) {
        if (gatewayURL == null) throw NullPointerException("gateway 无法获取到URL")
        bot.context["vertx"] = vertx

        logger.info("WebSocketClient[${client.hashCode()}] 准备访问WebSocketSever接入点: $gatewayURL")

        val uri = URI(gatewayURL!!)
        val port = when (uri.scheme) {
            "wss", "https" -> 443
            "ws", "http" -> 80
            else -> uri.port
        }.let {
            if (uri.port > 0) uri.port else it
        }

        WS?.close()
        logger.debug("ws服务器 host:${uri.host} port:$port path:${uri.path}")
        //开启 ws
        client.connect(port, uri.host, uri.path).onSuccess { ws ->
            bot.context["ws"] = ws
            WS = ws
            this.payloadCmdHandler = PayloadCmdHandler(bot, promise, ws)
            if (!bot.context.contains(handlerKey)) bot.context[handlerKey] = this.payloadCmdHandler
            logger.info("WebSocketClient[${client.hashCode()}] 完成创建 WebSocket[${ws.hashCode()}] : 链接服务器成功")
        }.onSuccess { ws ->
            val handler = this.payloadCmdHandler ?: bot.context.getValue<PayloadCmdHandler>(handlerKey)


            ws.handler { buffer ->
                vertx.executeBlockingKt {
                    kotlin.runCatching {
                        handler.handle(buffer, ws)
                    }.onFailure {
                        when (it) {
                            // 接收到处理器发出的重连申请
                            is WebSocketReconnectException -> {
                                logger.debug("WebSocket[${ws.hashCode()}]  准备重连 -> HttpClosedException: ${it.message}")
                                //广播机器人下线事件
                                GlobalEventBus.broadcastAuto(
                                    BotOfflineEvent(
                                        bot.botInfo,
                                        bot.context.get<Throwable>(throwableKey)
                                    )
                                )

                                reconnect(client, reconnect, if (retry < 0) retry - 1 else retry + 1)
                            }

                            else -> logger.error(
                                "WebSocket[${ws.hashCode()}]  位于网络层的阻塞线程捕获到了处理器的异常，这是不合理的！以下为捕获的异常",
                                it
                            )
                        }
                    }
                }
            }

            ws.closeHandler {
                handler.close()
                logger.info("WebSocketClient[${client.hashCode()}] 被关闭")
                //广播机器人下线事件
                GlobalEventBus.broadcastAuto(BotOfflineEvent(bot.botInfo, bot.context[throwableKey]))
            }

            ws.exceptionHandler {
                bot.context[throwableKey] = it
                when (it) {
                    is SocketException -> logger.error("WebSocket链接发生异常 (java.net.SocketException)", it)
                    is HttpClosedException -> {
                        logger.error("WebSocket链接因为异常被关闭 (io.vertx.core.http.HttpClosedException)", it)
                    }

                    else -> logger.error("WebSocket连接出现异常 ", it)
                }

                if (it is SocketException || it is HttpClosedException) reconnect(client, reconnect, retry)
            }
        }.onFailure {
            logger.warn("WebSocketClient[${client.hashCode()}] 启动失败,由于没有建立连接不予重连，请新建连接并保证网络畅通")
            logger.error("WebSocketClient[${client.hashCode()}] 启动失败", it)
            promise.tryFail(it)
        }
    }


    private fun reconnect(client: WebSocketClient, reconnect: Boolean, retry: Int) {
        logger.debug("WebSocketClient[${client.hashCode()}] 剩余重连次数: $retry")
        logger.debug("WebSocketClient[${client.hashCode()}] 是否允许重连: $reconnect")
        if (retry != 0 && reconnect) {
            reconnectHandler(client, retry).handle(0)
        } else {
            logger.warn("WebSocketClient[${client.hashCode()}] 重连达到极限，不再允许重连")
        }
    }

    private fun reconnectHandler(client: WebSocketClient, retry: Int, count: Int = 0): Handler<Long> {
        return Handler<Long> {
            vertx.executeBlockingKt {
                //检测网络是否通畅
                if (ping("223.5.5.5") || ping("114.114.114.114") || ping("8.8.8.8") || ping("208.67.222.222")) {
                    logger.debug("WebSocketClient[${client.hashCode()}] 重连中...")
                    connect(client, true, retry - 1)
                } else {
                    logger.warn("WebSocketClient[${client.hashCode()}] 网络不可用, 预计将在 ${reconnectTime}ms 后尝试重连[网络检测次数:$count]")
                    if (reconnectTime + 100 <= 3500) {
                        reconnectTime += 100
                    }
                    vertx.setTimer(reconnectTime, reconnectHandler(client, retry, count + 1))
                }
            }
        }
    }

    private fun ping(host: String = "223.5.5.5", port: Int = 53): Boolean {
        runCatching {
            val serverAddress = InetAddress.getByName(host)
            if (serverAddress.isReachable(port)) {
                return true // 如果可以到达，则认为网络可用
            }
        }
        return false // 如果有任何错误发生，假定网络不可用
    }

    /**
     * 关闭 WebSocketClient
     */
    fun close(): Future<Void> {
        val voidPromise = promise<Void>()
        voidPromise.tryComplete()
        return client?.close() ?: bot.context.get<WebSocket>("ws")?.close() ?: voidPromise.future()
    }

    override suspend fun stop() {
        close()
    }
}
