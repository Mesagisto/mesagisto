package io.github.itsusinn.extension.vertx.eventloop

import io.github.itsusinn.extension.log.logger
import io.vertx.core.Vertx

object EventBus

val vertx =
   Vertx.vertx()
      .exceptionHandler{
         EventBus.logger.error(it.message,it)
      }
val eventBus = vertx.eventBus()