package org.meowcat.minecraft.forward

import com.github.shynixn.mccoroutine.registerSuspendingEvents
import com.github.shynixn.mccoroutine.setSuspendingExecutor
import kotlinx.coroutines.*
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.message.FriendMessageEvent
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.TempMessageEvent
import net.mamoe.mirai.message.data.content
import org.bstats.bukkit.Metrics
import org.bukkit.plugin.java.JavaPlugin
import org.meowcat.minecraft.forward.BotDispatcher.speakers
import org.meowcat.minecraft.forward.BotDispatcher.target
import kotlin.collections.HashMap

class Forward : JavaPlugin() {

    companion object{

    }

    override fun onEnable() = launch {

        //bStats
        val pluginID = 9145
        Metrics(this@Forward,pluginID)

        subscribeAlways<GroupMessageEvent>(Dispatchers.Default) {
            if (bot.id!= BotDispatcher.listener) return@subscribeAlways
            //防止回环监听
            if(group.id == target){
                speakers.forEach { if (it.id == sender.id) return@subscribeAlways }
                broadcastMessage("<${this.sender.nameCardOrNick}> ${message.content}")
            }
        }
        subscribeAlways<MessageEvent>{
            if((this is FriendMessageEvent)||(this is TempMessageEvent)){
                this.sender.id

            }
        }
        logger.info("Forward is Loading")
        logger.info("GitHub: https://github.com/itsusinn/Minecraft-Forward")
        ConfigService.load()
        //注册消息监听器
        server.pluginManager.registerSuspendingEvents(MessageListener,plugin)
        //注册命令处理器
        server.getPluginCommand("forward")!!.setSuspendingExecutor(ForwardCommandExecutor)
    }

    override fun onDisable() {
        //保存配置
        ConfigService.save()
    }
}