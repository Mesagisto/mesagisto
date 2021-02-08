package itsusinn.extension.jackson

import io.vertx.core.buffer.Buffer

/**
 * Method to serialize instance into JSON content.
 * Note that the nullable [Any] is only for compatibility with generics
 * if the return value is null,it will throw [NullPointerException]
 */
fun Any?.writeAsString(): String? = kotlin.runCatching {
   writer.writeValueAsString(this)
}.getOrNull()
/**
 * a short way of [writeAsString]
 */
inline val Any?.asString: String?
   get() = kotlin.runCatching {
      writer.writeValueAsString(this)
   }.getOrNull()

/**
 * Method to serialize instance into JSON content.
 * Note that the nullable [Any] is only for compatibility with generics
 * if the return value is null,it will throw [NullPointerException]
 */
fun Any?.writeAsBytes(): ByteArray? = kotlin.runCatching {
   writer.writeValueAsBytes(this)
}.getOrNull()
/**
 * a short way of [writeAsBytes]
 */
inline val Any?.asBuffer: Buffer?
   get() {
      val bytes = this.asBytes
         ?: return null
      return Buffer.buffer(bytes)
   }

fun Any?.writeAsBuffer(): Buffer? {
   val bytes = this.asBytes
      ?: return null
   return Buffer.buffer(bytes)
}
/**
 * a short way of [writeAsBytes]
 */
inline val Any?.asBytes: ByteArray?
   get() = kotlin.runCatching {
      writer.writeValueAsBytes(this)
   }.getOrNull()

/**
 * Method to serialize instance into JSON content.
 * Note that the nullable [Any] is only for compatibility with generics
 * if the return value is null,it will throw [NullPointerException]
 */
fun Any?.writeAsPrettyString(): String? = kotlin.runCatching {
   prettyWriter.writeValueAsString(this)
}.getOrNull()
/**
 * a short way of [writeAsString]
 */
inline val Any?.asPrettyString: String?
   get() = kotlin.runCatching {
      prettyWriter.writeValueAsString(this)
   }.getOrNull()
