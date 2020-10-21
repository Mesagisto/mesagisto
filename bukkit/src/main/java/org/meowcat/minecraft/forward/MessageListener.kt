package org.meowcat.minecraft.forward

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.meowcat.minecraft.forward.BotDispatcher.speakers
import org.meowcat.minecraft.forward.BotDispatcher.target

object MessageListener:Listener{
    @EventHandler
    suspend fun onChat(event: AsyncPlayerChatEvent) {
        //没有speakers时直接返回
        if (speakers.isNotEmpty()) return
        val msg = event.message
        val senderName = event.player.name
        speakers.random().getGroup(target).sendMessage("<$senderName> $msg")
    }
}
