package org.meowcat.minecraft.forward

import com.github.shynixn.mccoroutine.registerSuspendingEvents
import com.github.shynixn.mccoroutine.setSuspendingExecutor
import kotlinx.coroutines.*
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.data.content
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.meowcat.minecraft.forward.BotDispatcher.speakers
import org.meowcat.minecraft.forward.data.ConfigService
import kotlin.collections.HashMap

class Forward : JavaPlugin() {

    companion object{
        //用于保存机器人创建者的map
        val operating by lazy { HashMap<Long,String>() }
    }

    override fun onEnable() = launch {

        //bStats
        val pluginID = 9145
        Metrics(this@Forward,pluginID)

        subscribeAlways<GroupMessageEvent>(Dispatchers.Default) {
            if (bot.id!= BotDispatcher.listener) return@subscribeAlways
            //防止回环监听
            when(group.id){
                BotDispatcher.target -> {
                    speakers.forEach { if (it.id == sender.id) return@subscribeAlways }
                    Bukkit.broadcastMessage("<${this.sender.nameCardOrNick}> ${message.content}")
                }

            }
        }
        logger.info("Forward is Loading")
        logger.info("GitHub: https://github.com/itsusinn/Minecraft-Forward")
        ConfigService.load()
        //注册消息监听器
        server.pluginManager.registerSuspendingEvents(MessageListener(),plugin)
        //注册命令处理器
        server.getPluginCommand("forward")!!.setSuspendingExecutor(ForwardCommandExecutor)
    }

    override fun onDisable() {
        //保存配置
        ConfigService.save()
    }
}