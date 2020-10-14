package org.meowcat.minecraft.forward.mirai

import net.mamoe.mirai.utils.MiraiLoggerPlatformBase
import org.bukkit.Bukkit
import java.util.logging.Level

class MineLogger(override val identity: String?) : MiraiLoggerPlatformBase(){
    override fun debug0(message: String?, e: Throwable?) {
        Bukkit.getLogger().log(Level.INFO,identity+message,e)
    }

    override fun error0(message: String?, e: Throwable?) {
        Bukkit.getLogger().log(Level.WARNING,identity+message,e)
    }

    override fun info0(message: String?, e: Throwable?) {
        Bukkit.getLogger().log(Level.INFO,identity+message,e)
    }

    override fun verbose0(message: String?, e: Throwable?) {
        Bukkit.getLogger().log(Level.INFO,identity+message,e)
    }

    override fun warning0(message: String?, e: Throwable?) {
        Bukkit.getLogger().log(Level.WARNING,identity+message,e)
    }

}