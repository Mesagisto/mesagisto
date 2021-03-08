package org.mesagisto.mcproxy

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.mesagisto.client.Logger
import org.mesagisto.client.MesagistoConfig
import org.mesagisto.client.Server
import org.mesagisto.client.utils.ConfigKeeper
import org.mesagisto.mcproxy.handlers.Receive
import org.mesagisto.mcproxy.platforms.BungeePlugin
import java.io.File
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

object Plugin : CoroutineScope {
  override val coroutineContext: CoroutineContext = EmptyCoroutineContext
  lateinit var bungee: BungeePlugin

  private var closed: Boolean = false

  private val CONFIG_KEEPER = ConfigKeeper.create(File("plugins/mesagisto/config.yml").toPath()) { RootConfig() }

  val CONFIG = CONFIG_KEEPER.value

  fun onLoad(bungee: BungeePlugin) {
    this.bungee = bungee
    Logger.bridgeToStd(bungee.logger)
    CONFIG_KEEPER.save()
    Template.init()
  }
  fun onEnable() {
    if (closed) {
      throw IllegalStateException("hot reload error")
    }
    if (!CONFIG.enable) {
      Logger.info { "Mesagisto信使未启用" }
      return
    }
    val config = MesagistoConfig.builder {
      name = "bungeecord"
      cipherKey = CONFIG.cipher.key
      remotes = CONFIG.centers
      packetHandler = Receive::packetHandler
      sameSideDeliver = false
    }

    launch {
      config.apply()
      Receive.recover()
    }
    bungee.proxy.pluginManager.registerListener(bungee, Listener)
    bungee.proxy.pluginManager.registerCommand(bungee, Command)
    Logger.info { "Mesagisto信使启用成功" }
  }
  fun onDisable() {
    CONFIG_KEEPER.save()
    if (CONFIG.enable) {
      Server.close()
    }
    closed = true
  }
}
