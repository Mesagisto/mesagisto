package org.meowcat.minecraft.forward

import com.github.shynixn.mccoroutine.registerSuspendingEvents
import com.github.shynixn.mccoroutine.setSuspendingExecutor
import kotlinx.coroutines.*
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.data.content
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.meowcat.minecraft.forward.data.ConfigService
import org.meowcat.minecraft.forward.mirai.BotLoginSolver
import kotlin.collections.HashMap

class Forward : JavaPlugin() {

    companion object{
        val configService = ConfigService.create()
        //bot调度器
        val botDispatcher by lazy { BotDispatcher.create() }
        //保存登录的机器人对象
        val allBots
            get() = botDispatcher.allBots
        //记录用于发言的bot 由调度器分配
        val speakers
            get() = botDispatcher.allBots
        //用于保存机器人创建者的map
        val operating by lazy { HashMap<Long,String>() }
    }

    override fun onEnable() = launch {
        subscribeAlways<GroupMessageEvent>(Dispatchers.Default) {
            if (bot.id!= botDispatcher.listener) return@subscribeAlways //防止重复监听
            when(group.id){
                botDispatcher.target -> {
                    botDispatcher.speakers.forEach { if (it.id == sender.id) return@subscribeAlways }
                    Bukkit.broadcastMessage("<${this.sender.nameCardOrNick}> ${message.content}")
                }

            }
        }
        logger.info("Forward is Loading")
        logger.info("GitHub: https://github.com/itsusinn/Minecraft-Forward")
        configService.load()
        //注册消息监听器
        server.pluginManager.registerSuspendingEvents(MessageListener(),plugin)
        //注册命令处理器
        server.getPluginCommand("forward")!!.setSuspendingExecutor(CommandExecutor())
    }

    override fun onDisable() {
        //保存配置
        configService.save()
    }
}