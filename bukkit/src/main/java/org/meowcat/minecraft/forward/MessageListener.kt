package org.meowcat.minecraft.forward

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class MessageListener:Listener{

    private val target  = 226556947L

    @EventHandler
    suspend fun onChat(event: AsyncPlayerChatEvent) {
        val msg = event.message
        val senderName = event.player.name
        if (Forward.speakers.isNotEmpty()) {
            Forward.speakers.values.random()
                    .getGroup(target).sendMessage("<$senderName> $msg")
        }
    }
}
