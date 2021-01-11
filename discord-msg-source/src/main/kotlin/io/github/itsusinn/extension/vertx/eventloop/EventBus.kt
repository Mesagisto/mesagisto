package io.github.itsusinn.extension.vertx.eventloop

import com.github.michaelbull.logging.InlineLogger
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import mu.KotlinLogging

private val logger = KotlinLogging.logger(Vertx::javaClass.name)

val vertx = Vertx.vertx()
   .exceptionHandler{
      logger.error(it) { it.message }
   }

val Dispatchers.Vertx: CoroutineDispatcher
   get() = vertx.dispatcher()

val eventBus: EventBus = vertx.eventBus()


