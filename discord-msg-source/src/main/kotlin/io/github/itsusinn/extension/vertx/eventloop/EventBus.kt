package io.github.itsusinn.extension.vertx.eventloop

import io.github.itsusinn.extension.log.Log
import io.github.itsusinn.extension.log.logger
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus


val vertx =
   Vertx.vertx().exceptionHandler{ Log.logger.error(it.message,it) }

val eventBus: EventBus = vertx.eventBus()

