package org.mesagisto.mcproxy
import net.md_5.bungee.api.connection.Connection

object MultiServer {
  private val servers by lazy { Plugin.bungee.proxy.servers }
  private val players by lazy { Plugin.bungee.proxy.players }
  fun getServerName(conn: Connection): String? =
    servers.firstNotNullOfOrNull {
      if (it.value.socketAddress == conn.socketAddress) it.value.name else null
    }

  fun getPlayer(conn: Connection) =
    players.firstOrNull {
      it.socketAddress == conn.socketAddress
    }

  fun broadcastOn(server: String, text: String) {
    players.forEach {
      if (it.server.info.name != server) return@forEach
      it.sendText(text)
    }
  }
}
