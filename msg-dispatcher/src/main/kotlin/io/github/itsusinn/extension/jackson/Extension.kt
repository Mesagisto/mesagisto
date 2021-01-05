package io.github.itsusinn.extension.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())


/**
 * Method to deserialize JSON content from given JSON content String.
 *
 * @throws JsonParseException if underlying input contains invalid content
 *    of type {@link JsonParser} supports (JSON for default case)
 * @throws JsonMappingException if the input JSON structure does not match structure
 *   expected for result type (or has other mismatch issues)
 */
inline fun <reified T> readValue(src:String): T = mapper.readValue(src,T::class.java)
/**
 * @throws IOException
 * @throws JsonParseException if underlying input contains invalid content
 *    of type {@link JsonParser} supports (JSON for default case)
 * @throws JsonMappingException if the input JSON structure does not match structure
 *   expected for result type (or has other mismatch issues)
 */
inline fun <reified T> readValue(src:ByteArray): T = mapper.readValue(src,T::class.java)