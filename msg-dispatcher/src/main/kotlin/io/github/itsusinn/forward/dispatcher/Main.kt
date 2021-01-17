package io.github.itsusinn.forward.dispatcher

import io.vertx.core.AsyncResult
import io.vertx.core.Vertx
import mu.KotlinLogging
import io.vertx.core.VertxOptions
import io.vertx.core.spi.cluster.ClusterManager
import io.vertx.spi.cluster.ignite.IgniteClusterManager


object Main {
   private val logger = KotlinLogging.logger {  }
   @JvmStatic
   fun main(args: Array<String>){

      val mgr: ClusterManager = IgniteClusterManager()
      val options = VertxOptions().setClusterManager(mgr)

      Vertx.clusteredVertx(options) { res ->
         if (res.succeeded()) {
            val vertx = res.result()
            vertx.exceptionHandler { e ->
               logger.error(e){ e.stackTrace }
            }
            vertx.deployVerticle(MainVerticle())
         } else {
            logger.error { " failed!" }
         }
      }
   }
}