@file:Suppress("UNUSED")
package io.github.itsusinn.mc.easyforward.extension



import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender



fun String.toTextComponent(color: ChatColor): TextComponent =
   TextComponent(this).apply { this.color = color }

fun broadcastMessage(message:String) =
   Bukkit.broadcastMessage(message)

inline fun <reified T> broadcastMessage(component:T) where T: BaseComponent =
   Bukkit.spigot().broadcast(component)

inline fun <reified T>  CommandSender.sendMessage(component: T) where T: BaseComponent=
   spigot().sendMessage(component)

inline fun <reified T>  CommandSender.sendMessage(vararg components: T) where T: BaseComponent =
   components.forEach { this.spigot().sendMessage(it) }

fun getCommandSender(name:String): CommandSender {
   return when(name){
      "CONSOLE" -> {
         Bukkit.getConsoleSender()
      }
      else -> {
         val trueName = name.substring(2)
         Bukkit.getPlayer(trueName) ?: Bukkit.getConsoleSender()
      }
   }
}

fun makeClickUrl(title:String, url:String): TextComponent {
   val message = TextComponent(title)
   message.color = ChatColor.YELLOW
   message.clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, url)
   return message
}