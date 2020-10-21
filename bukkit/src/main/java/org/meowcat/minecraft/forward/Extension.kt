@file:Suppress("UNUSED")
package org.meowcat.minecraft.forward
/*
 Some of code is from https://github.com/Shynixn/MCCoroutine
 */
import com.github.shynixn.mccoroutine.launchAsync
import com.github.shynixn.mccoroutine.launch
import com.github.shynixn.mccoroutine.asyncDispatcher
import com.github.shynixn.mccoroutine.minecraftDispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import net.mamoe.mirai.Bot
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import kotlin.coroutines.CoroutineContext

val Bot.captchaChannel by lazy { Channel<String>() }

val plugin
    get() = JavaPlugin.getPlugin(Forward::class.java)

fun launch(f: suspend CoroutineScope.() -> Unit) {
    JavaPlugin.getPlugin(Forward::class.java).launch(f)
}
fun launch(dispatcher: CoroutineContext, f: suspend CoroutineScope.() -> Unit){
    JavaPlugin.getPlugin(Forward::class.java).launch(dispatcher,f)
}
fun launchAsync(f: suspend CoroutineScope.() -> Unit) {
    JavaPlugin.getPlugin(Forward::class.java).launchAsync(f)
}
fun String.toTextComponent(color: ChatColor): TextComponent {
    return TextComponent(this).apply { this.color = color }
}

fun broadcastMessage(message:String){
    Bukkit.broadcastMessage(message)
}
fun broadcast(component: BaseComponent){
    Bukkit.broadcast(component)
}
val Dispatchers.Minecraft: CoroutineContext
    get() {
        return JavaPlugin.getPlugin(Forward::class.java).minecraftDispatcher
    }

val Dispatchers.Async: CoroutineContext
    get() {
        return JavaPlugin.getPlugin(Forward::class.java).asyncDispatcher
    }

val bukkitLogger
    get() = plugin.logger

val defaultConfig = """
    botList:
    - account: "123456789"
      passwordMD5: "9A0364B9E99BB480DD25E1F0284C8555"
    target: 123456789
""".trimIndent()

const val success = true
const val failure = false


