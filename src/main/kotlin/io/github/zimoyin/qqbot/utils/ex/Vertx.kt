package io.github.zimoyin.qqbot.utils.ex

import io.vertx.core.Future
import io.vertx.core.Vertx
import java.util.concurrent.Callable

/**
 *
 * @author : zimo
 * @date : 2023/12/09
 */

/**
 *
 * @param ordered 如果为 true，则如果在同一上下文中多次调用 executeBlocking，则该上下文的执行将按顺序执行，而不是并行执行。如果为 false，则它们将没有排序保证
 * @param blockingCode  代表要运行的阻塞代码的处理程序
 */
inline fun <T : Any> Vertx.executeBlockingKt(ordered: Boolean = true, crossinline blockingCode: () -> T): Future<T> {
    return executeBlocking(Callable {
        return@Callable blockingCode()
    }, ordered)
}
