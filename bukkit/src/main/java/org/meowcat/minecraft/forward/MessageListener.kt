package org.meowcat.minecraft.forward

import net.mamoe.mirai.containsGroup
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.meowcat.minecraft.forward.BotDispatcher.speakers

class MessageListener:Listener{

    private val target
        get() = BotDispatcher.target

    @EventHandler
    suspend fun onChat(event: AsyncPlayerChatEvent) {
        val msg = event.message
        val senderName = event.player.name
        if (speakers.isNotEmpty()) {
            var rBot = speakers.random()
            //如果随机到的这个bot没有加入群的话就再获取一个
            //是为了防止bot被踢出产生错误
            while (!rBot.containsGroup(target)){
                BotDispatcher.reDispatch()
                if (speakers.isEmpty()) return
                rBot = speakers.random()
            }
            rBot.getGroup(target).sendMessage("<$senderName> $msg")
        }
    }
}
