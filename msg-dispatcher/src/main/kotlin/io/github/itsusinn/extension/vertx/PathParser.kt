package io.github.itsusinn.extension.vertx

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.github.itsusinn.extension.jackson.readValue
import java.util.*




object PathParser {
   val decoder = Base64.getUrlDecoder()
   inline fun <reified T> parse(path:String):T = readValue<T>(
      decoder.decode(if (path.startsWith("/")) path.drop(0) else path)
   )
}