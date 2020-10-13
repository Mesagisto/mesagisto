package org.meowcat.minecraft.forward

import net.mamoe.mirai.Bot
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import kotlin.random.Random

class MessageListener:Listener{
    private val bots: HashMap<Long, Bot>
        get() = Forward.listeners

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
