@file:Suppress("UNUSED")
package org.meowcat.minecraft.forward

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

fun String.toTextComponent(color: ChatColor): TextComponent =
   TextComponent(this).apply { this.color = color }

fun broadcastMessage(message:String) =
   Bukkit.broadcastMessage(message)

fun broadcast(component: BaseComponent) =
   Bukkit.spigot().broadcast(component)

fun CommandSender.sendMessage(component: BaseComponent) =
   spigot().sendMessage(component)

fun CommandSender.sendMessage(vararg components: BaseComponent) =
   components.forEach { this.spigot().sendMessage(it) }

