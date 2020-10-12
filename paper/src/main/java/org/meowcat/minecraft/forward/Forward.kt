package org.meowcat.minecraft.forward

import kotlinx.coroutines.channels.Channel
import net.mamoe.mirai.Bot
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.PrintStream

class Forward : JavaPlugin() {
    internal companion object{
        val bots by lazy { HashSet<Bot>() }
        val captchaChannel by lazy { HashMap<Long,Channel<String>>() }
        val operating by lazy { HashMap<Long,String>() }
    }
    override fun onEnable() {
        logger.info("Forward Loading")
        Bukkit.getPluginManager().registerEvents(MessageListener(), this)
        Bukkit.getPluginCommand("forward")?.setExecutor(CommandExecutor())
    }

    override fun onDisable() {

    }
}