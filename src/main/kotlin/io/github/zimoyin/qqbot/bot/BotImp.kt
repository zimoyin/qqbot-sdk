package io.github.zimoyin.qqbot.bot

import io.github.zimoyin.qqbot.LocalLogger
import io.github.zimoyin.qqbot.bot.message.MessageChain
import io.github.zimoyin.qqbot.exception.HttpClientException
import io.github.zimoyin.qqbot.net.Token
import io.github.zimoyin.qqbot.net.bean.SendMessageResultBean
import io.github.zimoyin.qqbot.net.http.TencentOpenApiHttpClient
import io.github.zimoyin.qqbot.net.http.api.HttpAPIClient
import io.github.zimoyin.qqbot.net.http.api.accessToken
import io.github.zimoyin.qqbot.net.http.api.botInfo
import io.github.zimoyin.qqbot.net.webhook.WebHookConfig
import io.github.zimoyin.qqbot.net.webhook.WebHookHttpServer
import io.github.zimoyin.qqbot.net.websocket.WebsocketClient
import io.github.zimoyin.qqbot.utils.ex.awaitToCompleteExceptionally
import io.github.zimoyin.qqbot.utils.ex.isInitialStage
import io.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.http.WebSocket

/**
 *
 * @author : zimo
 * @date : 2023/12/06/23:55
 */
class BotImp(
    val token: Token,
    override val context: BotContent = BotContent(),
    override val config: BotConfig = BotConfigBuilder().setToken(token).build(),
) : Bot {
    private val logger = LocalLogger(BotImp::class.java)

    private var websocketClient: WebsocketClient? = null
    private var webHookHttpServer: WebHookHttpServer? = null

    private val vertx = config.vertx

    override var avatar: String = "not init"
        private set
    override var nick: String = "not init"
        private set
    override var channelBotId: String = "not init"
        private set
    override var unionOpenid: String = "not init"
        private set
    override var unionUserAccount: String = "not init"
        private set

    override val botInfo: BotInfo by lazy {
        BotInfo.create(this)
    }

    init {
        try {
            if (token.version == 2) HttpAPIClient.accessToken(token).awaitToCompleteExceptionally {
                updateInfo()
            }
            if (token.version == 1) updateInfo()
        } catch (e: NullPointerException) {
            throw HttpClientException("Unable to provide specific information about the robot", e)
        }
    }

    private fun updateInfo() {
        HttpAPIClient.botInfo(token).awaitToCompleteExceptionally().let { user ->
            avatar = user.avatar ?: ""
            nick = user.username
            unionOpenid = user.unionOpenID ?: ""
            unionUserAccount = user.unionUserAccount ?: ""
            channelBotId = user.id
            if (user.code != 0) throw HttpClientException("Unable to provide specific information about the robot: ${user.message}; ${user.errorMessage()}")
        }
    }

    override fun login(): Future<WebSocket> {
        return login(true)
    }

    override fun login(isVerifyHost: Boolean): Future<WebSocket> {
        val promise = Promise.promise<WebSocket>()

        val isForwarding = !TencentOpenApiHttpClient.isUseCustomHost
        if (isForwarding) logger.warn("QQ 官方机器人平台计划于 2024 年停止使用 WebSocket 协议，请使用 HTTP API 进行机器人操作。使用 start 进行启动")
        if (isForwarding) logger.warn("如果需要使用复用WebSocket，推荐使用 WebHook 开启 WebSocket 转发，让该ws连接 WebHook 开启的 ws")

        if (websocketClient != null) {
            return Future.failedFuture(IllegalStateException("Web socket has already been started"))
        }
        this.context["internal.promise"] = promise
        when (token.version) {
            2 -> {
                HttpAPIClient.accessToken(token).awaitToCompleteExceptionally()
                websocketClient = WebsocketClient(this, isVerifyHost, promise)
                logger.info("Vertx 部署Verticle： WebSocketClient")
                vertx.deployVerticle(websocketClient).onFailure {
                    if (promise.isInitialStage()) logger.error("无法启动 ws 客户端", it)
                    promise.tryFail(it)
                }
            }

            1 -> {
                websocketClient = WebsocketClient(this, isVerifyHost, promise)
                vertx.deployVerticle(websocketClient).onFailure {
                    promise.tryFail(it)
                    if (promise.isInitialStage()) logger.error("无法启动 ws 客户端", it)
                }
            }

            else -> {
                val exception = IllegalArgumentException("Unsupported version: ${token.version}")
                promise.isInitialStage().apply {
                    promise.tryFail(exception)
                    if (this) logger.error("无法获取到 Access Token，禁止启动 ws 客户端", exception)
                }
            }
        }

        if (websocketClient == null) {
            promise.tryFail(NullPointerException("WebsocketClient is null"))
        } else {
            this.context["internal.websocketClient"] = websocketClient
        }
        // 返回
        return promise.future()
    }

    override fun start(config: WebHookConfig?): Future<WebHookHttpServer> {
        val promise = promise<WebHookHttpServer>()
        webHookHttpServer = WebHookHttpServer(promise, this, config ?: WebHookConfig())
        vertx.deployVerticle(webHookHttpServer).onFailure {
            promise.tryFail(it)
        }
        return promise.future()
    }

    @Deprecated("The official has abandoned the WebSocket method")
    override fun close(): Future<Void> {
        val promise = promise<Void>()
        if (websocketClient != null) websocketClient?.close()?.onFailure {
            if (promise.isInitialStage()) {
                logger.error("Bot Close 失败", it)
            }
            promise.tryFail(it)
        }?.onSuccess {
            promise.tryComplete()
        }
        if (webHookHttpServer != null) webHookHttpServer?.close()?.onFailure {
            if (promise.isInitialStage()) {
                logger.error("Bot Close 失败", it)
            }
            promise.tryFail(it)
        }?.onSuccess {
            promise.tryComplete()
        }
        this.context.clear()
        logger.info("the bot[${this.config.token.appID}] 上下文被清空")
        return promise.future()
    }

    @Deprecated("Official ban on active sending")
    override fun send(message: MessageChain): Future<SendMessageResultBean> {
        throw IllegalStateException("You cannot send yourself a message by yourself")
    }

    override fun toString(): String {
        return "BotImp(nick='$nick', id='$id')"
    }


}
