@file:Suppress("NOTHING_TO_INLINE")

package org.meowcat.mesagisto.mirai

import com.luciad.imageio.webp.WebPReadParam
import kotlinx.coroutines.*
import org.mesagisto.client.Db
import org.mesagisto.client.Logger
import org.mesagisto.client.toByteArray
import org.mesagisto.client.toI32
import java.io.Closeable
import java.nio.file.Path
import javax.imageio.ImageIO
import javax.imageio.stream.FileImageInputStream
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

fun <T> ensureLazy(value: T) {}

@JvmName("isWebp-ext")
suspend fun Path.isWebp() = isWebp(this)

suspend fun isWebp(path: Path): Boolean = runInterruptible fn@{
  runCatching {
    path.inputStream().use {
      val iis = ImageIO.createImageInputStream(it)
      val ir = ImageIO.getImageReaders(iis)
      val next = ir.next()
      when (next.formatName) {
        "WebP" -> true
        else -> false
      }
    }
  }
}.getOrThrow()
suspend fun convertWebpToPng(from: Path, to: Path): Result<Unit> = withContext(Dispatchers.IO) {
  runCatching {
    val reader = ImageIO.getImageReadersByMIMEType("image/webp").next()
    val readParam = WebPReadParam().apply {
      isBypassFiltering = true
    }
    reader.input = FileImageInputStream(from.toFile())
    val image = reader.read(0, readParam)
    to.outputStream().use {
      ImageIO.write(image, "png", it)
    }
    (reader.input as Closeable).close()
    Logger.debug { "成功由WEBP转化为PNG." }
  }
}

inline fun Db.putMsgId(
  target: Long,
  remote: Int,
  local: Int
) = putMsgId(target.toByteArray(), remote.toByteArray(), local.toByteArray())
inline fun Db.putMsgId(
  target: Long,
  remote: ByteArray,
  local: Int
) = putMsgId(target.toByteArray(), remote, local.toByteArray())

inline fun switch(classLoader: ClassLoader, fn: () -> Result<Unit>): Result<Unit> {
  val origin = Thread.currentThread().contextClassLoader
  Thread.currentThread().contextClassLoader = classLoader
  return try {
    fn.invoke()
  } catch (t: Throwable) {
    Result.failure(t)
  } finally {
    Thread.currentThread().contextClassLoader = origin
  }
}
