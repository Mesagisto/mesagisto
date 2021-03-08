package org.meowcat.mesagisto.kato

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.plugin.java.JavaPlugin
import org.meowcat.mesagisto.kato.handlers.Listener
import org.meowcat.mesagisto.kato.handlers.Receive
import org.meowcat.mesagisto.kato.platform.JvmPlugin
import org.mesagisto.client.Logger
import org.mesagisto.client.MesagistoConfig
import org.mesagisto.client.Server
import org.mesagisto.client.utils.ConfigKeeper
import java.io.File
import kotlin.coroutines.EmptyCoroutineContext

object Plugin : JvmPlugin(), CoroutineScope {
  override val coroutineContext = EmptyCoroutineContext
  private lateinit var bukkit: JavaPlugin

  private var closed: Boolean = false

  private val CONFIG_KEEPER = ConfigKeeper.create(File("plugins/mesagisto/config.yml").toPath()) { RootConfig() }
  private val DATA_KEEPER = ConfigKeeper.create(File("plugins/mesagisto/data.yml").toPath()) { RootData() }
  val CONFIG = CONFIG_KEEPER.value
  val DATA = DATA_KEEPER.value
  override fun onLoad(bukkit: JavaPlugin): Result<Unit> = runCatching fn@{
    this.bukkit = bukkit
    Logger.bridgeToBukkit(Plugin.bukkit.logger)
    CONFIG_KEEPER.save()
    Template.init()
    return@fn
  }
  override fun onEnable() = runCatching {
    if (closed) {
      throw IllegalStateException("hot reload error")
    }
    val config = MesagistoConfig.builder {
      name = "bukkit"
      cipherKey = CONFIG.cipher.key
      remotes = CONFIG.centers
      packetHandler = Receive::packetHandler
      sameSideDeliver = false
    }
    launch {
      config.apply()
      Receive.recover()
    }
    bukkit.server.pluginManager.registerEvents(Listener, bukkit)
    Logger.info { "Mesagisto信使启用成功" }
  }

  override fun onDisable() = runCatching {
    // attention!! before this term, zip(jar) is closed
    // so, loading class before onDisable
    DATA_KEEPER.save()
    Server.close()
    closed = true
  }
}
