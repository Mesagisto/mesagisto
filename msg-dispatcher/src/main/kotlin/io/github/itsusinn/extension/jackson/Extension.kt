package io.github.itsusinn.extension.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

inline fun <reified T> readValue(src:String): T = mapper.readValue(src,T::class.java)
inline fun <reified T> readValue(src:ByteArray): T = mapper.readValue(src,T::class.java)