package io.github.zimoyin.qqbot.net.http

import io.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import io.github.zimoyin.qqbot.LocalLogger
import io.vertx.core.http.impl.headers.HeadersMultiMap
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions

/**
 *
 * @author : zimo
 * @date : 2024/05/12
 */
object TencentOpenApiHttpClient {
    private var isOptionsInitialized = false
    private val logger = LocalLogger(TencentOpenApiHttpClient::class.java)

    @JvmStatic
    val DefaultHeaders by lazy {
        HeadersMultiMap().apply {
            //通用头
        }
    }

    @JvmStatic
    var isSandBox = false
        set(value) {
            if (isOptionsInitialized) throw IllegalStateException("Options has been initialized. Please set up the sandbox environment before creating the bot")
            field = value
        }

    const val sandBoxHost = "sandbox.api.sgroup.qq.com"
    const val productionHost = "api.sgroup.qq.com"

    @JvmStatic
    var host = if (isSandBox) sandBoxHost else productionHost
        set(value) {
            if (isOptionsInitialized) throw IllegalStateException("Options has been initialized. Please set up the sandbox environment before creating the bot")
            if (field != productionHost && field != sandBoxHost) isCustomHost = true
            field = value
        }

    @JvmStatic
    fun setHost(path: String, prot: String) {
        host = "$path:$prot"
    }

    @JvmStatic
    fun setHost(path: String, prot: Int) {
        host = "$path:$prot"
    }

    @JvmStatic
    val webSocketForwardingAddress: String
        get() = "wss://${host}/${webSocketForwardingPath}"

    @JvmStatic
    var webSocketForwardingPath = "/websocket"

    /**
     * 是否存在自定义服务器地址
     */
    @JvmStatic
    var isCustomHost = false
        private set

    /**
     * 是否使用自定义服务器地址
     */
    @JvmStatic
    var isUseCustomHost = false

    private fun getTencentHost(): String {
        return if (isSandBox) sandBoxHost else productionHost
    }

    private val options: WebClientOptions by lazy {
        isOptionsInitialized = true
        WebClientOptions()
            .setUserAgent("java_qqbot_gf:1.2.6")
            .setDefaultHost(if (isUseCustomHost) host else getTencentHost())
            .setConnectTimeout(5000)
            .setKeepAlive(true)
            .setSsl(true)
            .setTrustAll(true)
            .setFollowRedirects(true)
            .setMaxRedirects(10)
            .setDefaultPort(443)
            .setPoolCleanerPeriod(5000)
            .setPoolEventLoopSize(32)
            .setMaxPoolSize(64)
            .setMaxWaitQueueSize(-1)
    }

    @JvmStatic
    val client: WebClient by lazy {
        WebClient.create(GLOBAL_VERTX_INSTANCE, options)
    }
}
