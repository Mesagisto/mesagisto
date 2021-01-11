package io.github.itsusinn.extension.jackson

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

/**
 * mapper instance
 * Feature:
 * accept empty string as null
 */
val mapper: ObjectMapper = ObjectMapper().apply {
   registerModule(KotlinModule())
   configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,true)
}
val writer = mapper.writerWithDefaultPrettyPrinter()

/**
 * Method to serialize instance into JSON content.
 * Note that the nullable [Any] is only for compatibility with generics
 * if the return value is null,it will throw [NullPointerException]
 */
fun Any?.writeValueAsString(): String = writer.writeValueAsString(this)

/**
 * Method to deserialize JSON content from given JSON content String.
 *
 * @throws JsonParseException if underlying input contains invalid content
 *    of type {@link JsonParser} supports (JSON for default case)
 * @throws JsonMappingException if the input JSON structure does not match structure
 *   expected for result type (or has other mismatch issues)
 */
inline fun <reified T> readValue(src:String): T? {
   try {
      return mapper.readValue(src,T::class.java)
   }catch (e:Exception){
      return null
   }
}
inline fun <reified T> readValue(src:ByteArray): T? {
   try {
      return mapper.readValue(src,T::class.java)
   }catch (e:Exception){
      e.printStackTrace()
      return null
   }
}