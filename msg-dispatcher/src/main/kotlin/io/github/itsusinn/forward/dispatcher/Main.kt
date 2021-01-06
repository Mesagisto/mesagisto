package io.github.itsusinn.forward.dispatcher

import io.github.itsusinn.extension.logger
import io.vertx.core.Vertx

object Main {
   @JvmStatic
   fun main(args: Array<String>){
      val vertx = Vertx.vertx()
      vertx.exceptionHandler {
         logger.error(it.message,it)
      }
      vertx.deployVerticle(MainVerticle())
   }
}