package io.github.zimoyin.qqbot.utils

import io.github.zimoyin.qqbot.GLOBAL_VERTX_INSTANCE
import io.github.zimoyin.qqbot.SystemLogger
import io.github.zimoyin.qqbot.annotation.UntestedApi
import io.github.zimoyin.qqbot.utils.ex.promise
import io.vertx.core.Context
import io.vertx.core.Future
import io.vertx.core.Future.await
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.impl.ContextImpl
import io.vertx.core.impl.WorkerPool
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.Consumer
import kotlin.reflect.KProperty

/**
 * 创建一个虚拟线程池
 */
val virtualThreadExecutor: ExecutorService? by lazy {
    runCatching {
        val clazz = Executors::class.java
        val method = clazz.getMethod("newVirtualThreadPerTaskExecutor")
        method.isAccessible = true
        method.invoke(null) as ExecutorService
    }.getOrNull()
}

/**
 * 启动一个虚拟线程，如果没有虚拟线程则启动一个协程
 */
fun <T> CoroutineScope.virtualThread(block: suspend CoroutineScope.() -> T): java.util.concurrent.Future<T> {
    val future = CompletableFuture<T>()

    val job = Job()
    if (virtualThreadExecutor != null) {
        return virtualThreadExecutor!!.submit {
            runBlocking(coroutineContext + job) {
                block()
            }
        } as java.util.concurrent.Future<T>
    }

    launch {
        kotlin.runCatching { block().apply { future.complete(this) } }.onFailure {
            future.completeExceptionally(it)
        }
    }
    return future
}

/**
 * 启动一个虚拟线程，如果没有虚拟线程则启动一个IO协程
 */
fun <T> virtualThread(block: suspend CoroutineScope.() -> T): java.util.concurrent.Future<T> {
    val future = CompletableFuture<T>()
    if (virtualThreadExecutor != null) {
        return virtualThreadExecutor!!.submit {
            runBlocking(block = block)
        } as java.util.concurrent.Future<T>
    }

    CoroutineScope(Dispatchers.Default).launch {
        kotlin.runCatching { block().apply { future.complete(this) } }.onFailure {
            future.completeExceptionally(it)
        }
    }
    return future
}


/**
 * 在后台线程执行非挂起的代码块。
 *
 * @author : zimo
 * @date : 2023/12/21
 * @param callback 需要在后台线程执行的非挂起代码块。
 * @return 一个 [Job] 对象，用于管理协程的生命周期。
 */
fun io(callback: suspend CoroutineScope.() -> Unit): Job = CoroutineScope(Dispatchers.IO).launch {
    callback()
}


/**
 * 在后台线程执行非挂起的代码块。
 *
 * @author : zimo
 * @date : 2023/12/21
 * @param callback 需要在后台线程执行的非挂起代码块。
 * @return 一个 [Job] 对象，用于管理协程的生命周期。
 */
fun cpu(callback: suspend CoroutineScope.() -> Unit): Job = CoroutineScope(Dispatchers.Default).launch {
    callback()
}

fun coroutine(callback: suspend CoroutineScope.() -> Unit): Job = CoroutineScope(Dispatchers.vertx()).launch {
    callback()
}


/**
 * 在后台线程执行挂起的代码块，并返回一个 [Deferred] 对象，用于获取执行结果。使用 await 可以挂起协程并等待结果
 * 注意调用 await 后不会阻塞线程但是会阻塞协程。
 * @author : zimo
 * @date : 2023/12/21
 * @param callback 需要在后台线程执行的挂起代码块。
 * @return 一个 [Deferred] 对象，可以用于等待执行完成并获取结果。
 */
fun <T> async(
    dispatcher: CoroutineDispatcher = Dispatchers.vertxWorker(),
    callback: suspend CoroutineScope.() -> T,
): Deferred<T> =
    CoroutineScope(dispatcher).async {
        callback()
    }


/**
 * 在后台线程执行挂起的代码块，并返回一个 [Future] 对象，用于获取执行结果。使用 await 可以挂起协程并等待结果
 */
fun <T> task(callback: suspend CoroutineScope.(Promise<T>) -> T): Future<T> {
    val promise = promise<T>()
    CoroutineScope(Dispatchers.vertxWorker()).launch {
        kotlin.runCatching {
            callback(promise)
        }.onFailure {
            promise.tryFail(it)
        }.onSuccess {
            promise.tryComplete(it)
        }
    }
    return promise.future()
}

/**
 * 创建一个线程，并执行一个代码块。
 */
fun thread(callback: () -> Unit): Thread {
    return Thread { callback() }.apply { start() }
}

/**
 * 获取一个与 Vertx 事件线程池对应的协程调度器。
 * 该调度器使用的是 vertx 事件循环器，因此禁止在该调度器中执行高CPU任务与阻塞任务
 * 对于网络，IO, 文件等操作，推荐使用 vertx 的异步方法，或者使用 vertx 的线程池
 */
fun Dispatchers.vertx(vertx: Vertx = GLOBAL_VERTX_INSTANCE): CoroutineDispatcher {
    return vertx.dispatcher()
}

/**
 * 获取一个与 Vertx 工作线程对应的协程调度器。
 * 该调度器是复用了 Vertx 工作线程池，因此可能会导致意外的调度错误
 */
fun Dispatchers.vertxWorker(vertx: Vertx = GLOBAL_VERTX_INSTANCE): CoroutineDispatcher {
    var context = Vertx.currentContext() ?: vertx.orCreateContext
    val worker = context.get<ExecutorCoroutineDispatcher>("worker_ExecutorCoroutineDispatcher") ?: run {
        SystemLogger.warn("你正在使用 Vertx 工作线程池转化的协程调度器，该调度器可能存在调度BUG，执行 Vertx 代码时可能导致上下文不匹配问题")
        val workerPoolField = try {
            if (context is ContextImpl) {
                return@run (context as ContextImpl).workerPool().executor().asCoroutineDispatcher()
            }
            context::class.java.getDeclaredField("workerPool").apply { isAccessible = true }
        } catch (e: NoSuchFieldException) {
            val delegateField = context::class.java.getDeclaredField("delegate").apply { isAccessible = true }
            context = delegateField.get(context) as Context
            context::class.java.getDeclaredField("workerPool").apply { isAccessible = true }
        }
        (workerPoolField.get(context) as WorkerPool).executor().asCoroutineDispatcher()
    }

    context.put("worker_ExecutorCoroutineDispatcher", worker)
    return worker
}

class Async {
    companion object {
        @JvmStatic
        val vertx = GLOBAL_VERTX_INSTANCE

        /**
         * 创建一个IO协程
         */
        @JvmStatic
        fun createCoroutineIO(callback: Runnable) = io {
            callback.run()
        }

        /**
         * 创建一个CPU协程
         */
        @JvmStatic
        fun createCoroutineCPU(callback: Runnable) = cpu {
            callback.run()
        }


        /**
         * 创建一个Vertx Worker协程
         */
        @JvmStatic
        fun createCoroutine(callback: Runnable) = coroutine {
            callback.run()
        }

        /**
         * 创建一个异步任务，并返回一个Promise对象，用于获取执行结果。使用 await 可以挂起协程并等待结果
         */
        @JvmStatic
        fun <T> createCoroutineTask(callback: Consumer<Promise<*>>) = task {
            callback.accept(promise<T>())
        }

        /**
         * 创建一个异步任务，并返回一个Deferred对象，用于获取执行结果。使用 await 可以挂起协程并等待结果
         */
        @JvmStatic
        fun createCoroutineAsync(callback: Runnable) = async {
            callback.run()
        }

        /**
         * 创建一个Worker线程
         */
        @JvmStatic
        fun createWorkerThread(callback: Runnable) {
            vertx.orCreateContext.get<ExecutorCoroutineDispatcher>("worker_ExecutorCoroutineDispatcher").executor.execute {
                callback.run()
            }
        }

        /**
         * 创建一个虚拟线程,如果没有虚拟线程则启动一个协程
         */
        @JvmStatic
        fun createVirtualThread(callback: Runnable) {
            virtualThread {
                callback.run()
            }
        }
    }
}

fun workerThread(callback: () -> Unit) {
    Async.createWorkerThread {
        callback()
    }
}

/**
 * 异步创建一个值，并在使用的时候等待其生产完成
 * 注意，不能生产空值
 * 例如：异步创建值，并在需要使用时等待其等待生产完成，如果已经生产完成就直接返回
 *     val image: BufferedImage by AsyncValue {
 *         ImageIO.read(File("E:\\仓库\\study\\散图\\F0FadEKaEAAm9_m.jpg")).apply {
 *             delay(1100)
 *             println("78")
 *         }
 *     }
 */
class AsyncValue<T>(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val producer: suspend () -> T,
) {
    /**
     * 非委托下可以访问
     * 是否已经生产完成
     */
    var finished = false
        private set

    /**
     * 非委托下可以访问
     * 产生的值，如果值还未生产完成、异常报错、产生Null，则返回null
     */
    var result: T? = null
        private set

    /**
     * 非委托下可以访问
     * 产生的异常，如果异常未产生、未异常报错则返回null
     */
    var exception: Throwable? = null
        private set

    private var production: Deferred<T?> = CoroutineScope(dispatcher).async {
        runCatching { producer() }
            .onFailure { exception = it }
            .onSuccess { result = result ?: it }
            .getOrNull()
            .apply { finished = true }
    }

    /**
     * 获取值，如果值还未生产完成，则挂起协程等待
     */
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val result = runBlocking { await() }
        if (result == null && exception != null) throw exception!!
        if (result == null) throw NullPointerException("The value[$property] produced is null")
        return result
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        runCatching { production.cancel() }
        result = value
    }

    /**
     * 获取值，如果值还未生产完成，则挂起协程等待
     */
    suspend fun await(): T? = result ?: production.await().apply {
        if (exception != null) throw exception!!
    }
}
