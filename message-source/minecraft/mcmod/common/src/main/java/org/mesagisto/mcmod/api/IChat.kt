package org.mesagisto.mcmod.api

import java.util.* // ktlint-disable no-wildcard-imports

interface IChat {

  fun broadcastMessage(message: String)
  fun registerChatHandler(callback: ChatHandler)
}

fun interface ChatHandler {
  fun handle(sender: String, content: String)
}
val ChatImpl by lazy {
  ServiceLoader.load(IChat::class.java).findFirst().get()
}
