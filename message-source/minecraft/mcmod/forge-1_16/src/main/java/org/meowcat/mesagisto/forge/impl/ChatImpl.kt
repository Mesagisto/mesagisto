package org.meowcat.mesagisto.forge.impl

import net.minecraft.network.chat.ChatType
import net.minecraft.network.chat.TextComponent
import net.minecraft.server.MinecraftServer
import net.minecraftforge.event.ServerChatEvent
import net.minecraftforge.fml.server.ServerLifecycleHooks
import org.mesagisto.mcmod.api.ChatHandler
import org.mesagisto.mcmod.api.IChat
import java.util.*

class IChatImpl : IChat by ChatImpl

object ChatImpl : IChat {
  val server: MinecraftServer by lazy { ServerLifecycleHooks.getCurrentServer() }
  private val handlers: MutableList<ChatHandler> = arrayListOf()

  fun deliverChatEvent(event: ServerChatEvent) {
    handlers.forEach {
      it.handle(event.player.name.string, event.message)
    }
  }
  override fun broadcastMessage(message: String) {
    server.playerList.broadcastMessage(TextComponent(message), ChatType.SYSTEM, UUID.randomUUID())
  }
  override fun registerChatHandler(callback: ChatHandler) {
    handlers.add(callback)
  }
}
