@file:Suppress("UNUSED")
package org.meowcat.mesagisto.kato.extension

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

fun String.toTextComponent(color: ChatColor): TextComponent =
  TextComponent(this).apply { this.color = color }

fun publish(message: String) =
  Bukkit.broadcastMessage(message)

inline fun <reified T> publish(component: T) where T : BaseComponent =
  Bukkit.spigot().broadcast(component)

inline fun <reified T> publish(component: T, permission: String) where T : BaseComponent =
  Bukkit.broadcast(component.toLegacyText(), permission)

inline fun <reified T> CommandSender.sendMessage(component: T) where T : BaseComponent =
  spigot().sendMessage(component)

inline fun <reified T> CommandSender.sendMessage(vararg components: T) where T : BaseComponent =
  components.forEach { this.spigot().sendMessage(it) }

// fun makeHoverClickUrl(title: String, url: String): TextComponent {
//  val message = TextComponent(title)
//  message.apply {
//    color = ChatColor.YELLOW
//    clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, url)
//    hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("点击打开链接")) fixme:1.12.2
//  }
//  return message
// }
