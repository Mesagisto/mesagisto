package io.github.itsusinn.forward.dispatcher

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Promise
import io.vertx.core.http.HttpServerOptions
import mu.KotlinLogging

private val logger = KotlinLogging.logger {  }

class MainVerticle: AbstractVerticle() {
   override fun start(startPromise: Promise<Void>) {
      vertx.deployVerticle(
         DispatcherVerticle::class.java.name,
         DeploymentOptions().setInstances(5)
      )
      vertx.deployVerticle(
         RegisterVerticle::class.java.name,
         DeploymentOptions().setInstances(2)
      )
      logger.info{ "Server has started!" }
   }
}