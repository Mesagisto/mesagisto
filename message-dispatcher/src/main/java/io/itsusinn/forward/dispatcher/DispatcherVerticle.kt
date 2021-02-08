package itsusinn.forward.dispatcher

import io.github.itsusinn.extension.json
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.CoroutineVerticle
import itsusinn.extension.base64.debase64
import itsusinn.extension.jackson.writeAsPrettyString
import itsusinn.extension.vertx.websocket.warp
import itsusinn.forward.dispatcher.repo.EndpointKeeper
import itsusinn.forward.dispatcher.repo.checkToken
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

inline fun <R> RoutingContext.fail(statusCode: Int, completion: () -> R): R {
   response().setStatusCode(statusCode).end()
   return completion.invoke()
}

class DispatcherVerticle : CoroutineVerticle() {

   override suspend fun start() {

      val httpServer = vertx.createHttpServer()
      val router = Router.router(vertx)

      router.route(HttpMethod.GET, "/ws").handler { ctx ->
         val params = ctx.queryParams()
         val address = params.get("address")?.debase64 ?: ctx.fail(403) { return@handler }
         val token = params.get("token")?.debase64 ?: ctx.fail(403) { return@handler }
         val name = params.get("name")?.debase64

         if (!checkToken(address, token)) {
            ctx.fail(403) { return@handler }
         }
         ctx.request().toWebSocket {
            if (!it.succeeded()) return@toWebSocket
            val ws = it.result().warp(
               vertx = vertx,
               keeper = EndpointKeeper,
               address = address,
               name = name
            )
         }
      }

      router.route(HttpMethod.GET, "/info").handler { ctx ->
         ctx.response().json().end(EndpointKeeper.writeAsPrettyString())
      }

      router.route(HttpMethod.POST, "/bind").handler { ctx ->
         val body = ctx.getBodyAsJson() ?: ctx.fail(400) { return@handler }
         val dialect = body.getString("dialect")
      }

      httpServer
         .requestHandler(router)
         .listen(1431)

      logger.info { "DispatcherVerticle start" }
   }
}
