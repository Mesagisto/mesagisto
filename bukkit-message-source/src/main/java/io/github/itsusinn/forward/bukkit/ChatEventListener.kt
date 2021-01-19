@file:Suppress("NOTHING_TO_INLINE")
package io.github.itsusinn.forward.bukkit

import mu.KotlinLogging
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

private val logger = KotlinLogging.logger {  }

internal object ChatEventListener:Listener{
   @EventHandler
   suspend fun onChat(event: AsyncPlayerChatEvent) {
      try {
         chatEventHandler?.invoke(event)
      }catch (e:Throwable){
         logger.error(e) { "Uncaught Exception \n${e.stackTrace}" }
      }
   }
   var chatEventHandler:(suspend (AsyncPlayerChatEvent) -> Unit)? = null
   inline fun chatEventHandler(
      noinline handler:(suspend (AsyncPlayerChatEvent) -> Unit)
   ){
      chatEventHandler = handler
   }
}