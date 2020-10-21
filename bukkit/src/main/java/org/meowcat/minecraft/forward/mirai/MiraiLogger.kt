package org.meowcat.minecraft.forward.mirai

import net.mamoe.mirai.utils.MiraiLoggerPlatformBase
import org.bukkit.Bukkit
import org.meowcat.minecraft.forward.bukkitLogger
import java.util.logging.Level

class MiraiLogger(override val identity: String?) : MiraiLoggerPlatformBase(){
    override fun debug0(message: String?, e: Throwable?) {
        bukkitLogger.log(Level.INFO,identity+message,e)
    }

    override fun error0(message: String?, e: Throwable?) {
        bukkitLogger.log(Level.WARNING,identity+message,e)
    }

    override fun info0(message: String?, e: Throwable?) {
        bukkitLogger.log(Level.INFO,identity+message,e)
    }

    override fun verbose0(message: String?, e: Throwable?) {
        bukkitLogger.log(Level.INFO,identity+message,e)
    }

    override fun warning0(message: String?, e: Throwable?) {
        bukkitLogger.log(Level.WARNING,identity+message,e)
    }

}