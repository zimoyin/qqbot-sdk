package io.github.zimoyin.qqbot.utils.ex

import io.vertx.core.buffer.Buffer
import java.nio.charset.Charset


/**
 * 将Buffer转换为文本
 */
fun Buffer.writeToText(charset: Charset = Charsets.UTF_8): String {
    return String(bytes, charset)
}

/**
 * 将Buffer转换为指定类型的对象
 */
fun <T> Buffer.mapTo(cls:Class<T>): T {
    return this.toJsonObject().mapTo(cls)
}
