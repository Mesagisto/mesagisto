package org.mesagisto.mcproxy

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.mesagisto.mcproxy.Plugin.CONFIG
import org.mesagisto.mcproxy.handlers.Receive
import kotlin.coroutines.CoroutineContext

typealias BungeeCommand = net.md_5.bungee.api.plugin.Command
object Command : BungeeCommand("msgist", "mesagisto", "信使"), CoroutineScope {
  override val coroutineContext: CoroutineContext
    get() = Plugin.coroutineContext
  override fun execute(sender: CommandSender, args: Array<String>) {
    sender as ProxiedPlayer
    when (args.getOrNull(0)) {
      "bind" -> {
        val channel = args.getOrNull(1) ?: return
        sender.bind(channel)
      }
      "unbind" -> sender.unbind()
      "help" -> sender.help()
      "status" -> sender.status()
    }
  }
  private fun ProxiedPlayer.bind(roomAddress: String) = launch {
    val serverName = server.info.name
    val before = CONFIG.bindings.put(serverName, roomAddress)
    if (before != null) {
      Receive.change(before, roomAddress)
      sendText("成功将子服务器:$serverName 的信使频道变更为$roomAddress")
    } else {
      Receive.add(roomAddress)
      sendText("成功将子服务器:$serverName 的信使频道设置为$roomAddress")
    }
  }
  private fun ProxiedPlayer.unbind() = launch {
    val serverName = server.info.name
    CONFIG.bindings.remove(serverName)
    Receive.del(serverName)
    sendText("已解绑子服务器的信使频道")
  }
  private fun ProxiedPlayer.help() {
    sendText(
      """
      未知指令
      ------  用法  ------
      /信使 绑定 [频道名]
      或 
      /msgist bind [频道名]
      例如
      /msgist bind 114514、/信使 绑定 114514 等
      ------  列表  ------
      /msgist help = /信使 帮助
      /msgist bind = /信使 绑定
      /msgist unbind = /信使 解绑
      /msgist about = /信使 关于
      /msgist status = /信使 状态
      """.trimIndent()
    )
  }
  private fun ProxiedPlayer.status() {
    // val serverName = server.info.name
    // TODO: 2022/7/14 More Info
    sendText("唔... 也许是在正常运行?")
  }
}
