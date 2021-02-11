package itsusinn.forward.dispatcher

import io.itsusinn.forward.dispatcher.DispatcherVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.spi.cluster.ClusterManager
import io.vertx.spi.cluster.ignite.IgniteClusterManager
import mu.KotlinLogging

object Main {
   private val logger = KotlinLogging.logger { }
   @JvmStatic
   fun main(args: Array<String>) {

      val mgr: ClusterManager = IgniteClusterManager()
      val options = VertxOptions().setClusterManager(mgr)

      Vertx.clusteredVertx(options) { res ->
         if (res.succeeded()) {
            val vertx = res.result()
            vertx.exceptionHandler { e ->
               logger.error(e) { e.stackTrace }
            }
            vertx.deployVerticle(DispatcherVerticle::class.java.name, DeploymentOptions().setInstances(5))
         } else {
            logger.error { " failed!" }
         }
      }
   }
}
