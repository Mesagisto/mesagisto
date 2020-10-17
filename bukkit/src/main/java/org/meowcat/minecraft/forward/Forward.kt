package org.meowcat.minecraft.forward

import com.github.shynixn.mccoroutine.registerSuspendingEvents
import com.github.shynixn.mccoroutine.setSuspendingExecutor
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import net.mamoe.mirai.Bot
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.data.content
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.meowcat.minecraft.forward.data.Agent
import org.meowcat.minecraft.forward.data.ConfigService
import org.meowcat.minecraft.forward.mirai.BotLoginSolver
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class Forward : JavaPlugin() {

    companion object{
        val configService:ConfigService
            get() {
                var instance:ConfigService
                runBlocking { instance = ConfigService.create() }
                return instance
            }

        //bot调度器
        val botDispatcher by lazy { BotDispatcher.create() }

        //保存登录的机器人对象
        val allBots
            get() = botDispatcher.allBots

        //记录用于发言的bot 后期分配
        val speakers
            get() = botDispatcher.allBots

        //用于保存机器人创建者的map
        val operating by lazy { HashMap<Long,String>() }
        //是否是第一次加载
        var firstLoad:Boolean = true
    }

    override fun onEnable() = launch {
        subscribeAlways<GroupMessageEvent>(Dispatchers.Default) {
            when(group.id){
                botDispatcher.target ->
                    Bukkit.broadcastMessage("<${this.sender.nameCardOrNick}> ${message.content}")
            }
        }
        logger.info("Forward Loading")
        //注册消息监听器
        server.pluginManager.registerSuspendingEvents(MessageListener(),plugin)
        //注册命令处理器
        server.getPluginCommand("forward")!!.setSuspendingExecutor(CommandExecutor())

    }

    override fun onDisable() = launch{
        BotLoginSolver.logoutAll(allBots)
        //保存配置
        configService.save()
    }
}