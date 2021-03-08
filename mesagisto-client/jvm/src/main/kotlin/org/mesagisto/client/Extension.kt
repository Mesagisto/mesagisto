package org.mesagisto.client

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import kotlin.coroutines.CoroutineContext

suspend inline fun <R> withCatch(
  context: CoroutineContext,
  crossinline block: suspend CoroutineScope.() -> R,
): Result<R> =
  withContext(context) fn@{
    return@fn try {
      Result.success(block())
    } catch (e: Throwable) {
      Result.failure(e)
    }
  }

fun Int.toByteArray(): ByteArray =
  ByteBuffer.allocate(4)
    .putInt(this)
    .array()

fun Long.toByteArray(): ByteArray =
  ByteBuffer.allocate(8)
    .putLong(this)
    .array()

fun ByteArray.toI64(): Long? =
  runCatching {
    ByteBuffer.wrap(this)
      .getLong(0)
  }.getOrNull()

fun ByteArray.toI32(): Int? =
  runCatching {
    ByteBuffer.wrap(this)
      .getInt(0)
  }.getOrNull()

fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
