package org.meowcat.mesagisto.kato

import org.mesagisto.client.ILogger
import org.mesagisto.client.LogLevel
import org.mesagisto.client.Logger
import java.nio.ByteBuffer
import java.util.*
import java.util.logging.Level

typealias StdLogger = java.util.logging.Logger
typealias HandlebarsTemplate = com.github.jknack.handlebars.Template
fun Logger.bridgeToBukkit(impl: StdLogger) {
  level = when (impl.level) {
    Level.ALL -> LogLevel.TRACE
    Level.FINE -> LogLevel.TRACE
    Level.INFO -> LogLevel.TRACE
    Level.WARNING -> LogLevel.WARN
    Level.SEVERE -> LogLevel.ERROR
    Level.OFF -> LogLevel.ERROR
    else -> { LogLevel.INFO }
  }
  provider = object : ILogger {
    override fun log(level: LogLevel, msg: String) {
      when (level) {
        LogLevel.TRACE -> impl.fine(msg)
        LogLevel.DEBUG -> impl.finer(msg)
        LogLevel.INFO -> impl.info(msg)
        else -> impl.info(msg)
      }
    }
  }
}

object UuidUtils {
  fun asUuid(bytes: ByteArray): UUID {
    val bb: ByteBuffer = ByteBuffer.wrap(bytes)
    val firstLong: Long = bb.long
    val secondLong: Long = bb.long
    return UUID(firstLong, secondLong)
  }

  fun asBytes(uuid: UUID): ByteArray {
    val bb: ByteBuffer = ByteBuffer.wrap(ByteArray(16))
    bb.putLong(uuid.mostSignificantBits)
    bb.putLong(uuid.leastSignificantBits)
    return bb.array()
  }
}
fun UUID.asBytes(): ByteArray = UuidUtils.asBytes(this)

fun String.stripColor(): String = replace(Regex("\\u00A7[0-9A-FK-OR]", RegexOption.IGNORE_CASE), "")
