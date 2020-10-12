package org.meowcat.minecraft.forward

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import kotlin.coroutines.CoroutineContext

class MessageListener:Listener{
    private val bots:HashSet<Bot>
        get() = Forward.bots

    private val target  = 226556947L

    @EventHandler
    suspend fun onChat(event: AsyncPlayerChatEvent) {
        val msg = event.message
        val senderName = event.player.name
        if (bots.isNotEmpty()) {
            bots.random().getGroup(target).sendMessage("<$senderName> $msg")
        }
    }
}
