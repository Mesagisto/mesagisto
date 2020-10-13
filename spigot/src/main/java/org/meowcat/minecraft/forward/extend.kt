package org.meowcat.minecraft.forward
/*
 Some of code is from https://github.com/Shynixn/MCCoroutine
 */
import com.github.shynixn.mccoroutine.launchAsync
import com.github.shynixn.mccoroutine.launch
import com.github.shynixn.mccoroutine.asyncDispatcher
import com.github.shynixn.mccoroutine.minecraftDispatcher
import kotlinx.coroutines.*
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import kotlin.coroutines.CoroutineContext

fun launch(f: suspend CoroutineScope.() -> Unit) {
    JavaPlugin.getPlugin(Forward::class.java).launch(f)
}
fun launchAsync(f: suspend CoroutineScope.() -> Unit) {
    JavaPlugin.getPlugin(Forward::class.java).launchAsync(f)
}

val Dispatchers.Minecraft: CoroutineContext
    get() {
        return JavaPlugin.getPlugin(Forward::class.java).minecraftDispatcher
    }

val Dispatchers.Async: CoroutineContext
    get() {
        return JavaPlugin.getPlugin(Forward::class.java).asyncDispatcher
    }

val plugin:Forward
    get() = JavaPlugin.getPlugin(Forward::class.java)

fun FileConfiguration.setMap(path:String, map:Map< String, Any> ): ConfigurationSection {
    return this.createSection(path, map)
}
fun FileConfiguration.getMap(path:String): MutableMap<String, Any>? {
    return this.getConfigurationSection(path)?.getValues(false)
}
val logger
    get() = Bukkit.getLogger()
