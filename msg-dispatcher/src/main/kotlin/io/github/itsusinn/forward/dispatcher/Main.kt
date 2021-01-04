package io.github.itsusinn.forward.dispatcher

import io.vertx.core.Vertx

object Main {
   @JvmStatic
   fun main(args: Array<String>){
      val vertx = Vertx.vertx()
      vertx.deployVerticle(MainVerticle())
   }
}