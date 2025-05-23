package io.github.zimoyin.qqbot.net.http.api

import io.github.zimoyin.qqbot.net.Token
import io.github.zimoyin.qqbot.net.http.addRestfulParam
import io.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Future
import io.vertx.kotlin.core.json.jsonObjectOf

/**
 *
 * @author : zimo
 * @date : 2024/12/27
 */
fun HttpAPIClient.replyInteractions(token: Token, interactionId: String, code: Int): Future<Void> {
    val promise = promise<Void>()
    API.ReplyInteractions
        .putHeaders(token.getHeaders())
        .addRestfulParam(interactionId)
        .sendJsonObject(jsonObjectOf("code" to code))
        .bodyJsonHandle(promise, "ReplyInteractions", "send interactions error") {
            if (it.result) {
                promise.tryComplete()
            } else {
                promise.tryFail(it.errorMessage)
            }
        }
    return promise.future()
}
