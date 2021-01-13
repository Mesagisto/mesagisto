package io.github.itsusinn.extension.vertx

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.github.itsusinn.extension.base64.debase64
import io.github.itsusinn.extension.jackson.readValue
import java.util.*

/**
 * @param T pojo class
 * @param path json url path
 */
inline fun <reified T> parsePath(path:String):T? = readValue<T>(
   (if (path.startsWith("/")) path.drop(1) else path).debase64
)