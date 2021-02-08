package io.itsusinn.extension.jackson

/**
 * Method to serialize instance into JSON content.
 * Note that the nullable [Any] is only for compatibility with generics
 * if the return value is null,it will throw [NullPointerException]
 */
fun Any?.writeAsString(): String? = kotlin.runCatching {
   writer.writeValueAsString(this)
}.getOrElse {
   jacksonLogger.error(it) { it.stackTrace }
   null
}
/**
 * a short way of [writeAsString]
 */
inline val Any?.asString: String?
   get() = kotlin.runCatching {
      writer.writeValueAsString(this)
   }.getOrElse {
      jacksonLogger.error(it) { it.stackTrace }
      null
   }

/**
 * Method to serialize instance into JSON content.
 * Note that the nullable [Any] is only for compatibility with generics
 * if the return value is null,it will throw [NullPointerException]
 */
fun Any?.writeAsBytes(): ByteArray? = kotlin.runCatching {
   writer.writeValueAsBytes(this)
}.getOrElse {
   jacksonLogger.error(it) { it.stackTrace }
   null
}

/**
 * a short way of [writeAsBytes]
 */
inline val Any?.asBytes: ByteArray?
   get() = kotlin.runCatching {
      writer.writeValueAsBytes(this)
   }.getOrElse {
      jacksonLogger.error(it) { it.stackTrace }
      null
   }

/**
 * Method to serialize instance into JSON content.
 * Note that the nullable [Any] is only for compatibility with generics
 * if the return value is null,it will throw [NullPointerException]
 */
fun Any?.writeAsPrettyString(): String? = kotlin.runCatching {
   prettyWriter.writeValueAsString(this)
}.getOrElse {
   jacksonLogger.error(it) { it.stackTrace }
   null
}
/**
 * a short way of [writeAsString]
 */
inline val Any?.asPrettyString: String?
   get() = kotlin.runCatching {
      prettyWriter.writeValueAsString(this)
   }.getOrElse {
      jacksonLogger.error(it) { it.stackTrace }
      null
   }
