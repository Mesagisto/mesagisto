package io.github.itsusinn.forward.bukkit

import mu.KotlinLogging
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

private val logger = KotlinLogging.logger {  }

internal object AsyncPlayerChatEventListener:Listener{

   @EventHandler
   suspend fun onChat(event: AsyncPlayerChatEvent) {

   }


}