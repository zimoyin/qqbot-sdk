package io.github.zimoyin.qqbot

import io.vertx.core.logging.VertxLoggerFormatter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.logging.*

object SystemLogger : LocalLogger("System")


open class LocalLogger(name: String) {
    constructor(clazz: Class<*>) : this(clazz.name)
    constructor(obj: Any) : this(obj::class.java.name)

    companion object {
        init {
            changeJULogging { VertxLoggerFormatter().format(it) }
        }

        @JvmStatic
        @JvmOverloads
        fun getLogger(name: String? = runCatching { Thread.currentThread().stackTrace[2].className }.getOrNull()): LocalLogger {
            return LocalLogger(name ?: "System")
        }

        /**
         * 判断项目中是否有slf4j的实现类
         */
        @JvmStatic
        fun isSlf4jImplClassExists(): Boolean {
            return try {
                Class.forName("org.slf4j.impl.StaticLoggerBinder")
                true
            } catch (e: ClassNotFoundException) {
                false
            }
        }

        @JvmStatic
        val vertxFormatter = fun(record: LogRecord): String {
            return VertxLoggerFormatter().format(record)
        }

        @JvmStatic
        @JvmOverloads
        fun changeJULogging(
            // 日志级别
            level: Level = Level.INFO,
            // 获取根记录器
            rootLogger: java.util.logging.Logger = java.util.logging.Logger.getLogger(""),
            levelMap: HashMap<String, Level> = hashMapOf(),
            formatter: (LogRecord) -> String = { record ->
                String.format(
                    "%1\$tF %1\$tT [%4\$s] %5\$s %n",
                    Date(record.millis),
                    record.sourceClassName,
                    record.sourceMethodName,
                    record.level.name,
                    record.message
                )
            },
        ) {
            // 设置全局日志级别
            rootLogger.setLevel(level)

            // 获取控制台处理器
            var consoleHandler: Handler? = null
            for (handler in rootLogger.handlers) {
                if (handler is ConsoleHandler) {
                    consoleHandler = handler
                    break
                }
            }

            if (consoleHandler != null) {
                // 设置控制台处理器的日志级别
                consoleHandler.level = level

                consoleHandler.filter = Filter { record ->
                    for ((key, value) in levelMap) {
                        val d = value.intValue() <= record.level.intValue()
                        if (record.sourceClassName?.startsWith(key, true) == true) {
                            return@Filter d
                        }
                        if (record.loggerName.startsWith(key, true)) {
                            return@Filter d
                        }
                    }
                    true
                }
                // 设置自定义的格式化器
                consoleHandler.formatter = object : SimpleFormatter() {
                    @Synchronized
                    override fun format(record: LogRecord): String? {
                        return formatter(record)
                    }
                }
            }
        }
    }

    private val SystemLogger1: Logger? by lazy {
        if (isSlf4jImplClassExists()) LoggerFactory.getLogger(name) else null
    }

    private val SystemLogger2: io.vertx.core.impl.logging.Logger by lazy {
        io.vertx.core.impl.logging.LoggerFactory.getLogger(name)
    }

    fun error(throwable: Throwable) {
        if (SystemLogger1 != null) {
            SystemLogger1!!.error("", throwable)
        } else {
            SystemLogger2.error("", throwable)
        }
    }

    fun error(message: String = "", throwable: Throwable) {
        if (SystemLogger1 != null) {
            SystemLogger1!!.error(message, throwable)
        } else {
            SystemLogger2.error(message, throwable)
        }
    }

    fun error(message: String, vararg strs: Any?) {
        if (SystemLogger1 != null) {
            SystemLogger1!!.error(message, *strs)
        } else {
            SystemLogger2.error(formatMessage(message, *strs))
        }
    }

    fun warn(message: String = "", throwable: Throwable) {
        if (SystemLogger1 != null) {
            SystemLogger1!!.warn(message, throwable)
        } else {
            SystemLogger2.warn(message, throwable)
        }
    }

    fun warn(message: String, vararg strs: Any?) {
        if (SystemLogger1 != null) {
            SystemLogger1!!.warn(message, *strs)
        } else {
            SystemLogger2.warn(formatMessage(message, *strs))
        }
    }

    fun warn(throwable: Throwable) {
        if (SystemLogger1 != null) {
            SystemLogger1!!.info("", throwable)
        } else {
            SystemLogger2.info("", throwable)
        }
    }

    fun info(message: String, throwable: Throwable) {
        if (SystemLogger1 != null) {
            SystemLogger1!!.info(message, throwable)
        } else {
            SystemLogger2.info(message, throwable)
        }
    }

    fun info(message: String, vararg strs: Any?) {
        if (SystemLogger1 != null) {
            SystemLogger1!!.info(message, *strs)
        } else {
            SystemLogger2.info(formatMessage(message, *strs))
        }
    }

    fun info(throwable: Throwable) {
        if (SystemLogger1 != null) {
            SystemLogger1!!.info("", throwable)
        } else {
            SystemLogger2.info("", throwable)
        }
    }

    fun debug(message: String, throwable: Throwable) {
        if (SystemLogger1 != null) {
            SystemLogger1!!.debug(message, throwable)
        } else {
            SystemLogger2.debug(message, throwable)
        }
    }

    fun debug(message: String, vararg strs: Any?) {
        if (SystemLogger1 != null) {
            SystemLogger1!!.debug(message, *strs)
        } else {
            SystemLogger2.debug(formatMessage(message, *strs))
        }
    }

    fun debug(throwable: Throwable) {
        if (SystemLogger1 != null) {
            SystemLogger1!!.debug("", throwable)
        } else {
            SystemLogger2.debug("", throwable)
        }
    }

    fun trace(message: String, vararg strs: Any?) {
        if (SystemLogger1 != null) {
            SystemLogger1!!.trace(message, *strs)
        } else {
            SystemLogger2.trace(formatMessage(message, *strs))
        }
    }

    fun trace(throwable: Throwable) {
        if (SystemLogger1 != null) {
            SystemLogger1!!.trace("", throwable)
        } else {
            SystemLogger2.trace("", throwable)
        }
    }

    fun formatMessage(template: String, vararg args: Any?): String {
        var result = template
        var index = 0
        while (index < args.size && "{}" in result) {
            result = result.replaceFirst("{}", args[index].toString(), ignoreCase = false)
            index++
        }
        return result
    }
}
