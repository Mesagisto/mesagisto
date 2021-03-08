package org.meowcat.mesagisto.mirai

import kotlinx.coroutines.*
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.extension.PluginComponentStorage
import net.mamoe.mirai.console.permission.AbstractPermitteeId
import net.mamoe.mirai.console.permission.Permission
import net.mamoe.mirai.console.permission.PermissionService
import net.mamoe.mirai.console.plugin.PluginManager
import net.mamoe.mirai.console.plugin.id
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.NudgeEvent
import org.meowcat.mesagisto.mirai.handlers.Receive
import org.meowcat.mesagisto.mirai.handlers.sendHandler
import org.mesagisto.client.*
import org.mesagisto.client.utils.ConfigKeeper
import org.mesagisto.mirai_message_source.BuildConfig
import javax.imageio.ImageIO
import kotlin.io.path.*

object Plugin : KotlinPlugin(
  JvmPluginDescription(
    id = "org.mesagisto.mirai-message-source",
    name = "Mesagisto-Mirai",
    version = BuildConfig.VERSION,
  ),
) {
  private val eventChannel = globalEventChannel()
  private val listeners: MutableList<Listener<*>> = arrayListOf()
  private val Config_Keeper by lazy { ConfigKeeper.create(Path("config/mesagisto/config.yml")) { RootConfig() } }
  val Config by lazy { Config_Keeper.value }
  override fun PluginComponentStorage.onLoad() = runCatching {
    // prepare for next version
    val oldConfigs = arrayListOf(
      Path("config/org.meowcat.mesagisto/mesagisto.yml"),
      Path("config/org.mesagisto.mirai-message-source/config.yml"),
    )
    for (oldConfig in oldConfigs) {
      if (oldConfig.exists()) {
        val newConfig = Path("config/mesagisto/config.yml")
        newConfig.parent.createDirectories()
        oldConfig.moveTo(newConfig, true)
        oldConfig.parent.toFile().deleteRecursively()
      }
    }
    ensureLazy(Config)
    Config.migrate()
  }.onFailure {
    println(it) // TODO will it fails again?
  }.getOrDefault(Unit)
  override fun onEnable() {
    logger.info("正在加载Webp解析库 & LevelDB")
    // SPI And JNI related things
    switch(jvmPluginClasspath.pluginClassLoader) {
      Class.forName("org.sqlite.JDBC")
      ImageIO.scanForPlugins()
      Result.success(Unit)
    }.getOrThrow()
    logger.info("正在桥接信使日志系统")
    Logger.bridgeToMirai(logger)
    val config = MesagistoConfig.builder {
      name = "mirai"
      cipherKey = Config.cipher.key
      proxyEnable = Config.proxy.enable
      proxyUri = Config.proxy.address
      remotes = Config.centers
      sameSideDeliver = true
      packetHandler = Receive::packetHandler
      overrideCenter = Config.override_center
    }

    launch {
      config.apply()
      Receive.recover()
    }

    listeners.apply {
      add(eventChannel.subscribeAlways(::sendHandler, EventPriority.LOWEST))
      add(eventChannel.subscribeAlways(MiraiBot::botOnline))
    }
    if (Config.switch.nudge) {
      eventChannel.subscribeAlways<NudgeEvent> {
        if (Bot.getInstanceOrNull(target.id) != null) {
          subject.sendMessage("唔...可能是在正常运行？")
        }
      }
    }
    CommandManager.registerCommand(Command)
    val service: PermissionService<Permission> = PermissionService.INSTANCE as PermissionService<Permission>
    if (
      PluginManager.plugins.find {
        it.id == "io.github.karlatemp.luckperms-mirai"
      } != null
    ) {
      Logger.info { "检测到LuckPerms-Mirai, 信使不再管理自身权限." }
    } else if (Config.perm.strict) {
      Logger.info { "信使的严格模式已开启, 信使仅对名单内用户指令作出响应." }
      runCatching {
        service.cancel(AbstractPermitteeId.AnyUser, Plugin.parentPermission, true)
      }
      Config.perm.users.forEach { user ->
        service.permit(AbstractPermitteeId.parseFromString("u$user"), Plugin.parentPermission)
      }
    } else {
      Logger.info { "信使的严格模式已关闭, 信使指令可被任意用户调用, 但敏感操作仅允许群组管理员进行." }
      runCatching {
        service.cancel(AbstractPermitteeId.AnyUser, Plugin.parentPermission, true)
      }
      service.permit(AbstractPermitteeId.AnyUser, Plugin.parentPermission)
    }
    if (
      PluginManager.plugins.find {
        it.id == "net.mamoe.mirai.console.chat-command"
      } == null
    ) {
      Logger.error { "注册指令成功, 但依赖需要 chat-command,否则无法在聊天环境内执行命令" }
    } else {
      Logger.info { "注册指令成功" }
    }
    Logger.info { "Mirai信使已启用" }
  }

  override fun onDisable() {
    listeners.forEach {
      it.complete()
    }
    Config_Keeper.save()
    CommandManager.unregisterCommand(Command)
    Logger.info { "Mirai信使已禁用" }
  }
}
