package io.github.itsusinn.forward.dispatcher

import io.vertx.core.Vertx
import mu.KotlinLogging

object Main {
   private val logger = KotlinLogging.logger {  }
   @JvmStatic
   fun main(args: Array<String>){
      val vertx = Vertx.vertx()
      vertx.exceptionHandler { e ->
         logger.error(e){ e.stackTrace }
      }
      vertx.deployVerticle(MainVerticle())
   }
}