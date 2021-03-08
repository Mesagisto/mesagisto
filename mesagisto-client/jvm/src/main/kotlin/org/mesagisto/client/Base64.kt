package org.mesagisto.client

import java.util.Base64

object Base64 {
  private val decoder = Base64.getUrlDecoder()
  private val encoder = Base64.getUrlEncoder().withoutPadding()
  fun encodeToString(str: String): String {
    return encoder.encodeToString(str.toByteArray())
  }
  fun encodeToString(bytes: ByteArray): String {
    return encoder.encodeToString(bytes)
  }
  fun encode(bytes: ByteArray): ByteArray {
    return encoder.encode(bytes)
  }
  fun encode(str: String): ByteArray {
    return encoder.encode(str.toByteArray())
  }

  fun decode(str: String): Result<ByteArray> = runCatching {
    decoder.decode(str)
  }
  fun decode(bytes: ByteArray): Result<ByteArray> = runCatching {
    decoder.decode(bytes)
  }
}
