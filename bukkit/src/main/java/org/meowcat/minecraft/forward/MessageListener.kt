package org.meowcat.minecraft.forward

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.kodein.di.DI
import org.kodein.di.instance

class MessageListener(di:DI):Listener{

   private val bd:BotDispatcher by di.instance()

    @EventHandler
    suspend fun onChat(event: AsyncPlayerChatEvent) {
        //没有speakers时直接返回
        if (bd.speakers.isEmpty()) return
        val msg = event.message
        val senderName = event.player.name
        bd.speakers.random().getGroup(bd.target).sendMessage("<$senderName> $msg")
    }
}