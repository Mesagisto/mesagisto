package io.github.itsusinn.forward.dispatcher

import io.github.itsusinn.extension.base64.base64
import io.github.itsusinn.extension.json
import io.github.itsusinn.extension.vertx.websocket.pingBuffer
import io.github.itsusinn.extension.vertx.websocket.warp
import io.github.itsusinn.forward.dispatcher.data.PathArgu
import io.github.itsusinn.forward.dispatcher.repo.ConnectionRepo
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

val connMapper = ConnectionRepo()

class DispatcherVerticle: CoroutineVerticle() {
   override suspend fun start(){

      val httpServer = vertx.createHttpServer()
      val router = Router.router(vertx)

      router
         .route(HttpMethod.GET,"/ws")
         .handler { ctx ->
            val params = ctx.queryParams()
            val appID = params.get("app_id") ?: run {
               ctx.fail(403)
               return@handler
            }
            val channelID = params.get("channel_id") ?: run {
               ctx.fail(403)
               return@handler
            }
            val token = params.get("token") ?: run{
               ctx.fail(403)
               return@handler
            }
            if (!checkToken(appID, channelID, token)) {
               ctx.fail(403)
               return@handler
            }
            val address = "${appID}.${channelID}"
            ctx.request().toWebSocket {
               if (it.succeeded()){
                  val ws = it.result().warp(vertx,EndpointKeeper,address)
               }
            }

         }

      router.route(HttpMethod.GET,"/info").handler { ctx ->
         ctx.response().json().end(connMapper.status())
      }

      httpServer
         .requestHandler(router)
         .listen(1431)

      logger.info{"DispatcherVerticle start"}
   }

}


