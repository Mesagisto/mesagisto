package io.itsusinn.extension.jackson

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import mu.KotlinLogging
import java.text.SimpleDateFormat
import java.util.*

val jacksonLogger = KotlinLogging.logger { }

/**
 * add functional support for jackson
 */
object JacksonExtension

val mapper: ObjectMapper = ObjectMapper().apply {
   registerModule(KotlinModule())

   // Beijing China Std Time: GMT+8
   setTimeZone(TimeZone.getTimeZone("GMT+8"))
   setDateFormat(SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))

   configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
   configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

   configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
}

val prettyWriter = mapper.writerWithDefaultPrettyPrinter()
val writer = mapper.writer()
val reader = mapper.reader()
