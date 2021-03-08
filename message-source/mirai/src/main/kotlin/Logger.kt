package org.meowcat.mesagisto.mirai // ktlint-disable filename

import net.mamoe.mirai.utils.MiraiLogger
import org.mesagisto.client.ILogger
import org.mesagisto.client.LogLevel
import org.mesagisto.client.Logger

fun Logger.bridgeToMirai(impl: MiraiLogger) {
  // modify the receiver's field 'level' to the mirai log level
  level = LogLevel.TRACE
//  level = if (impl.isVerboseEnabled) {
//    LogLevel.TRACE
//  } else if (impl.isDebugEnabled) {
//    LogLevel.DEBUG
//  } else if (impl.isInfoEnabled) {
//    LogLevel.INFO
//  } else if (impl.isWarningEnabled) {
//    LogLevel.WARN
//  } else {
//    LogLevel.ERROR
//  }
  provider = object : ILogger {
    override fun log(level: LogLevel, msg: String) {
      when (level) {
        LogLevel.TRACE -> impl.info(msg)
        LogLevel.DEBUG -> impl.info(msg)
        LogLevel.INFO -> impl.info(msg)
        LogLevel.WARN -> impl.warning(msg)
        LogLevel.ERROR -> impl.error(msg)
        else -> impl.info(msg)
      }
    }
  }
}
