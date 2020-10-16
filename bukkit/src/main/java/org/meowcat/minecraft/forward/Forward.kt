package org.meowcat.minecraft.forward

import com.github.shynixn.mccoroutine.registerSuspendingEvents
import com.github.shynixn.mccoroutine.setSuspendingExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
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
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class Forward : JavaPlugin() {

    companion object{
        lateinit var configService:ConfigService

        //保存登录的机器人对象
        val allBots by lazy { HashMap<Long,Bot>() }
        //记录用于监听的bot 后期分配
        val listeners by lazy { HashMap<Agent,Bot>() }
        //记录用于发言的bot 后期分配
        val speakers by lazy { HashMap<Agent,Bot>() }
        //用于保存机器人创建者的map
        val operating by lazy { HashMap<Long,String>() }
        //保存所有类型的群聊
        var target = 12345678L
        //是否是第一次加载
        var firstLoad:Boolean = true
    }

    override fun onEnable() {
        launch {
            val defer = async(Dispatchers.Default) {
                configService = ConfigService.create()
            }
            defer.await()
        }
        launch {
            subscribeAlways<GroupMessageEvent>(Dispatchers.Default) {
                when(group.id){
                    target -> Bukkit.broadcastMessage("<${this.sender.nameCardOrNick}> ${message.content}")
                }
            }
        }

        logger.info("Forward Loading")
        //注册消息监听器
        server.pluginManager.registerSuspendingEvents(MessageListener(),this)

        //注册命令处理器
        server.getPluginCommand("forward")!!.setSuspendingExecutor(CommandExecutor())

    }

    override fun onDisable() {
    }

    @Serializable
    enum class Type(private val type: String) {
        LISTEN("LISTEN"),SPEAK("SPEAK"),BOTH("BOTH"),NONE("NONE");
        override fun toString(): String {
            return this.type
        }
    }
}