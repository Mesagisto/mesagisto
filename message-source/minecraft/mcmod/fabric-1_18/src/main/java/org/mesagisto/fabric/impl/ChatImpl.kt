package org.mesagisto.fabric.impl

import net.minecraft.network.chat.ChatType
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextComponent
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import org.mesagisto.mcmod.api.ChatHandler
import org.mesagisto.mcmod.api.IChat
import java.util.* // ktlint-disable no-wildcard-imports

class IChatImpl : IChat by ChatImpl

object ChatImpl : IChat {
  lateinit var server: MinecraftServer
  private val handlers: MutableList<ChatHandler> = arrayListOf()

  fun deliverChatEvent(player: ServerPlayer, content: Component) {
    handlers.forEach {
      it.handle(player.name.string, content.string)
    }
  }
  override fun broadcastMessage(message: String) {
    server.playerList.broadcastMessage(TextComponent(message), ChatType.SYSTEM, UUID.randomUUID())
  }
  override fun registerChatHandler(callback: ChatHandler) {
    handlers.add(callback)
  }
}
