@file:Suppress("NOTHING_TO_INLINE", "unused")

package org.mesagisto.client
enum class LogLevel {
  TRACE, DEBUG, INFO, WARN, ERROR
}
interface ILogger {
  fun log(level: LogLevel, msg: String)
}

object Logger {
  var level: LogLevel = LogLevel.TRACE
  var provider: ILogger? = null

  inline fun trace(msg: () -> String) {
    if (LogLevel.TRACE >= level) {
      provider?.log(LogLevel.TRACE, msg()) ?: println(msg())
    }
  }

  inline fun debug(msg: () -> String) {
    if (LogLevel.DEBUG >= level) {
      provider?.log(LogLevel.DEBUG, msg()) ?: println(msg())
    }
  }

  inline fun info(msg: () -> String) {
    if (LogLevel.INFO >= level) {
      provider?.log(LogLevel.INFO, msg()) ?: println(msg())
    }
  }

  inline fun warn(msg: () -> String) {
    if (LogLevel.WARN >= level) {
      provider?.log(LogLevel.WARN, msg()) ?: println(msg())
    }
  }

  inline fun error(msg: () -> String) {
    if (LogLevel.ERROR >= level) {
      provider?.log(LogLevel.ERROR, msg()) ?: println(msg())
    }
  }
  inline fun error(e: Throwable) = error { "错误 ${e.message} \n ${e.stackTraceToString()}" }
}
