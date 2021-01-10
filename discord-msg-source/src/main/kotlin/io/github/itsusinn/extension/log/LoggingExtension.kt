package io.github.itsusinn.extension.log

import com.github.michaelbull.logging.InlineLogger
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap


private val loggerCache = ConcurrentHashMap<Any, InlineLogger>()

/**
 * Lazily evaluated logger,
 * Can reduce the spending of splicing String.
 * Note that due to cache lookup spending,
 * this extended value should only be called once in a instance.
 */
fun Any.staticInlineLogger(): InlineLogger{
   return loggerCache.getOrPut(this::javaClass){ InlineLogger(this::class) }
}

//   implementation("org.slf4j:slf4j-api:1.7.30")
//   implementation("com.michael-bull.kotlin-inline-logger:kotlin-inline-logger:1.0.2")
//   implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.14.0")
//   implementation("org.apache.logging.log4j:log4j-core:2.14.0")
//   implementation("org.apache.logging.log4j:log4j-api:2.14.0")
object Log{
   val logger = staticInlineLogger()
}