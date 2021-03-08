package org.mesagisto.mcmod

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.mesagisto.client.Logger
import org.mesagisto.client.MesagistoConfig
import org.mesagisto.client.Server
import org.mesagisto.client.utils.ConfigKeeper
import org.mesagisto.mcmod.api.ChatImpl
import org.mesagisto.mcmod.api.CompatImpl
import org.mesagisto.mcmod.handlers.Receive
import org.mesagisto.mcmod.handlers.send
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.io.path.Path

object ModEntry : CoroutineScope {

  override val coroutineContext = EmptyCoroutineContext

  private val configKeeper by lazy { ConfigKeeper.create(Path("mods/mesagisto/config.yml")) { RootConfig() } }
  private val dataKeeper by lazy { ConfigKeeper.create(Path("mods/mesagisto/data.yml")) { RootData() } }
  val CONFIG by lazy { configKeeper.value }
  val DATA by lazy { dataKeeper.value }

  fun onEnable() {
    val logger = CompatImpl.getLogger()
    Logger.bridgeToLog4j(logger)

    if (!CONFIG.enable) {
      Logger.info { "信使插件未启用" }
      return
    }
    configKeeper.save()
    ChatImpl.registerChatHandler { sender, content ->
      launch {
        send(sender, content)
      }
    }
    val config = MesagistoConfig.builder {
      name = "mcmod"
      remotes = CONFIG.centers
      cipherKey = CONFIG.cipher.key
      sameSideDeliver = false
      packetHandler = Receive::packetHandler
    }
    launch {
      config.apply()
      Receive.recover()
    }
  }
  fun onDisable() {
    Server.close()
    dataKeeper.save()
  }

  fun onServerChat(
    sender: String,
    content: String
  ) = runBlocking {
    if (!CONFIG.enable) return@runBlocking
    send(sender, content)
  }
}
