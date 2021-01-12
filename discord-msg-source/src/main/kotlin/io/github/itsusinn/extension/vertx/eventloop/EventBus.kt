package io.github.itsusinn.extension.vertx.eventloop

import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import mu.KotlinLogging

private val logger = KotlinLogging.logger(Vertx::javaClass.name)

/**
 * Warning: this is just a short way of local use of vertx.
 * Never use this in server-side
 */
val vertx = Vertx.vertx()
   .exceptionHandler{
      logger.error(it) { it.message }
   }
/**
 * Warning: this is just a short way of local use of vertx.
 * Never use this in server-side
 */
val Dispatchers.Vertx: CoroutineDispatcher
   get() = vertx.dispatcher()
/**
 * Warning: this is just a short way of local use of vertx.
 * Never use this in server-side
 */
val eventBus: EventBus = vertx.eventBus()


