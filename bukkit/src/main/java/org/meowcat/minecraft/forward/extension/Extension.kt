@file:Suppress("UNUSED")
package org.meowcat.minecraft.forward.extension

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

fun String.toTextComponent(color: ChatColor): TextComponent {
    return TextComponent(this).apply { this.color = color }
}

fun broadcastMessage(message:String){
    Bukkit.broadcastMessage(message)
}
fun broadcast(component: BaseComponent){
    Bukkit.spigot().broadcast(component)
}
fun CommandSender.sendMessage(component: BaseComponent){
   this.spigot().sendMessage(component)
}
fun CommandSender.sendMessage(vararg components: BaseComponent){
   this.spigot().sendMessage(*components)
}

val defaultConfig = """
    botList:
    - account: "123456789"
      passwordMD5: "9A0364B9E99BB480DD25E1F0284C8555"
    target: 123456789
""".trimIndent()

const val success = true
const val failure = false


