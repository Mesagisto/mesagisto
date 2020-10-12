package org.meowcat.minecraft.forward

import com.github.shynixn.mccoroutine.registerSuspendingEvents
import com.github.shynixn.mccoroutine.setSuspendingExecutor
import kotlinx.coroutines.channels.Channel
import net.mamoe.mirai.Bot
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Forward : JavaPlugin() {
    internal companion object{
        val bots by lazy { HashSet<Bot>() }
        val captchaChannel by lazy { HashMap<Long,Channel<String>>() }
        val operating by lazy { HashMap<Long,String>() }
    }
    override fun onEnable() {

        logger.info("Forward Loading")

        //注册消息监听器
        server.pluginManager.registerSuspendingEvents(MessageListener(),this)

        //注册命令处理器
        server.getPluginCommand("forward")?.setSuspendingExecutor(CommandExecutor())
    }

    override fun onDisable() {
    }
}