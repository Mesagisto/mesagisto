package io.github.itsusinn.forward.dispatcher

import io.github.itsusinn.extension.base64.base64
import io.github.itsusinn.extension.base64.debase64
import io.github.itsusinn.extension.jackson.writeAsPrettyString
import io.github.itsusinn.extension.json
import io.github.itsusinn.extension.vertx.websocket.pingBuffer
import io.github.itsusinn.extension.vertx.websocket.warp
import io.github.itsusinn.forward.dispatcher.data.PathArgu
import io.github.itsusinn.forward.dispatcher.repo.EndpointKeeper
import io.github.itsusinn.forward.dispatcher.repo.checkToken
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.http.toWebSocketAwait
import io.vertx.kotlin.core.json.Json
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import mu.KotlinLogging
import java.util.concurrent.atomic.AtomicInteger

private val logger = KotlinLogging.logger {  }

inline fun <R> RoutingContext.fail(statusCode:Int, completion:()->R):R {
   response().setStatusCode(statusCode).end()
   return completion.invoke()
}

class DispatcherVerticle: CoroutineVerticle() {

   override suspend fun start(){

      val httpServer = vertx.createHttpServer()
      val router = Router.router(vertx)

      router.route(HttpMethod.GET,"/ws").handler { ctx ->
         val params = ctx.queryParams()
         val address = params.get("address")?.debase64 ?: ctx.fail(403){ return@handler }
         val token = params.get("token")?.debase64 ?: ctx.fail(403){ return@handler }
         val name = params.get("name")?.debase64

         if (!checkToken(address, token)) {
            ctx.fail(403){ return@handler }
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

      router.route(HttpMethod.GET,"/info").handler { ctx ->
         ctx.response().json().end(EndpointKeeper.writeAsPrettyString())
      }

      router.route(HttpMethod.POST,"/bind").handler { ctx ->
         val body = ctx.getBodyAsJson() ?: ctx.fail(400) { return@handler }
         val dialect = body.getString("dialect")
      }

      httpServer
         .requestHandler(router)
         .listen(1431)

      logger.info{"DispatcherVerticle start"}
   }

}


