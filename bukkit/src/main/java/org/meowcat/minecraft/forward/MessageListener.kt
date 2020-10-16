package org.meowcat.minecraft.forward

import net.mamoe.mirai.containsGroup
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class MessageListener:Listener{

    private val target
        get() = Forward.botDispatcher.target

    @EventHandler
    suspend fun onChat(event: AsyncPlayerChatEvent) {
        val msg = event.message
        val senderName = event.player.name
        if (Forward.speakers.isNotEmpty()) {
            var rBot = Forward.speakers.random()
            //如果随机到的这个bot没有加入群的话就再获取一个
            while (!rBot.containsGroup(target)){
                rBot = Forward.speakers.random()
            }
            rBot.getGroup(target).sendMessage("<$senderName> $msg")
        }
    }
}
