package org.mesagisto.fabric.impl

import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.mesagisto.mcmod.api.ChatHandler
import org.mesagisto.mcmod.api.IChat

class IChatImpl : IChat by ChatImpl

object ChatImpl : IChat {
  lateinit var server: MinecraftServer
  private val handlers: MutableList<ChatHandler> = arrayListOf()

  fun deliverChatEvent(player: ServerPlayerEntity, content: Text) {
    handlers.forEach {
      it.handle(player.name.string, content.string)
    }
  }
  override fun broadcastMessage(message: String) {
    server.playerManager.broadcast(Text.literal(message), false)
  }
  override fun registerChatHandler(callback: ChatHandler) {
    handlers.add(callback)
  }
}
