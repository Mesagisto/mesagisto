package io.github.itsusinn.extension.jackson


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
fun Any?.writeAsStringOrFail(): String = writer.writeValueAsString(this)

/**
 * a short way of [writeAsString]
 */
inline val Any?.asString:String?
   get() = runCatching {
      writer.writeValueAsString(this)
   }.getOrElse {
      jacksonLogger.error(it) { it.stackTrace }
      null
   }
inline val Any?.asStringOrFail:String
   get() = writer.writeValueAsString(this)





/**
 * Method to serialize instance into JSON content.
 * Note that the nullable [Any] is only for compatibility with generics
 * if the return value is null,it will throw [NullPointerException]
 */
fun Any?.writeAsBytes(): ByteArray? = runCatching {
   writer.writeValueAsBytes(this)
}.getOrElse {
   jacksonLogger.error(it) { it.stackTrace }
   null
}
fun Any?.writeAsBytesOrFail(): ByteArray = writer.writeValueAsBytes(this)

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
inline val Any?.asBytesOrFail: ByteArray?
   get() = writer.writeValueAsBytes(this)







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
fun Any?.writeAsPrettyStringOrFail(): String = prettyWriter.writeValueAsString(this)
/**
 * a short way of [writeAsString]
 */
inline val Any?.asPrettyString:String?
   get() = kotlin.runCatching {
      prettyWriter.writeValueAsString(this)
   }.getOrElse {
      jacksonLogger.error(it) { it.stackTrace }
      null
   }
inline val Any?.asPrettyStringOrFail:String
   get() = prettyWriter.writeValueAsString(this)