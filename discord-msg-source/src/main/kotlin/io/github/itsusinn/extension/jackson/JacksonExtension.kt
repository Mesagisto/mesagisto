package io.github.itsusinn.extension.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())
val writer = mapper.writerWithDefaultPrettyPrinter()
/**
 * Method to serialize instance into JSON content
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