package io.github.itsusinn.extension.forward.data

import io.ktor.client.call.*
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.launch
import mu.KotlinLogging
import java.util.concurrent.atomic.AtomicBoolean

fun DefaultClientWebSocketSession.warp():KtorWebsocket = KtorWebsocket(this)

private val logger = KotlinLogging.logger {  }

class KtorWebsocket(
   session: DefaultClientWebSocketSession
):ClientWebSocketSession, DefaultWebSocketSession by session {
   override val call: HttpClientCall = session.call
   private val closeSignal = AtomicBoolean(false)
   fun isClosed():Boolean = closeSignal.get()
   init {
      launch {
         session.apply {
            try {
               for (frame in incoming){
                  when(frame){
                     is Frame.Text -> {
                        textFrameHandler?.invoke(frame)
                     }
                     is Frame.Pong -> {
                        pongFrameHandler?.invoke(frame)
                     }
                     else -> { TODO("Maybe it is needed to impl") }
                  }
               }
            } catch (e: ClosedReceiveChannelException) {
               if (!closeSignal.acquire){
                  closeSignal.set(true)
                  closeHandler?.invoke()
               }
            } catch (e: Throwable) {
               uncaughtErrorHandler?.invoke(e)
            }
         }
      }
   }
   override suspend fun send(frame: Frame) {
      try {
         outgoing.send(frame)
      }catch (e:Throwable){
         close(CloseReason(CloseReason.Codes.GOING_AWAY,"Cannot send Frame"))
         if (!closeSignal.acquire){
            closeSignal.set(true)
            closeHandler?.invoke()
         }
      }
   }

   private var textFrameHandler:(suspend (Frame.Text) -> Unit)? = null
   fun textFrameHandler(handler:suspend (Frame.Text) -> Unit){
      textFrameHandler = {
         try {
            handler.invoke(it)
         }catch (e:Throwable){
            uncaughtErrorHandler?.invoke(e)
         }
      }
   }

   private var pongFrameHandler:(suspend (Frame.Pong) -> Unit)? = null
   fun pongFrameHandler(handler:suspend (Frame.Pong) -> Unit){
      pongFrameHandler = {
         try {
            handler.invoke(it)
         }catch (e:Throwable){
            uncaughtErrorHandler?.invoke(e)
         }
      }
   }

   private var closeHandler:(suspend () -> Unit)? = null
   fun closeHandler(handler: suspend () -> Unit){
      closeHandler = {
         try {
            handler.invoke()
         }catch (e:Throwable){
            uncaughtErrorHandler?.invoke(e)
         }
         textFrameHandler = null
         pongFrameHandler = null
         closeHandler = null
         uncaughtErrorHandler = null
      }
   }

   private var uncaughtErrorHandler:((Throwable) -> Unit)? =
      { logger.error(it) { "uncaughtError ${it.message}\n"+it.stackTrace } }
   fun uncaughtErrorHandler(handler:(Throwable) -> Unit){
      uncaughtErrorHandler = {
         try {
            handler.invoke(it)
         }catch (e:Throwable){
            logger.error(it) { "uncaughtError ${it.message}\n"+it.stackTrace }
         }
      }
   }
}