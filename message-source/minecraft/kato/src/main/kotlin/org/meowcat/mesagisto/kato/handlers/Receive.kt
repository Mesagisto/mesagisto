package org.meowcat.mesagisto.kato.handlers

import org.bukkit.Bukkit
import org.meowcat.mesagisto.kato.Plugin
import org.meowcat.mesagisto.kato.Plugin.CONFIG
import org.meowcat.mesagisto.kato.Template.renderMessage
import org.mesagisto.client.Base64
import org.mesagisto.client.Logger
import org.mesagisto.client.Server
import org.mesagisto.client.data.Message
import org.mesagisto.client.data.MessageType
import org.mesagisto.client.data.Packet
import org.mesagisto.client.utils.ControlFlow
import org.mesagisto.client.utils.Either
import org.mesagisto.client.withCatch

object Receive {
  suspend fun recover() {
    add(CONFIG.channel)
  }
  suspend fun add(roomAddress: String) {
    val roomId = Server.roomId(roomAddress)
    Server.sub(roomId, "mesagisto")
  }
  suspend fun packetHandler(pkt: Packet): Result<ControlFlow<Packet, Unit>> = withCatch(Plugin.coroutineContext) fn@{
    if (pkt.ctl != null) {
      return@fn ControlFlow.Continue(Unit)
    }
    pkt.decrypt()
      .onFailure {
        Logger.warn { "数据解密失败" }
      }
      .onSuccess {
        when (it) {
          is Either.Left -> {
            if (!it.value.from.contentEquals(CONFIG.target.toByteArray())) {
              msgHandler(it.value, "mesagisto").onFailure { e -> Logger.error(e) }
            }
          }
          is Either.Right -> return@fn ControlFlow.Break(pkt)
        }
      }
    return@fn ControlFlow.Continue(Unit)
  }
}
fun msgHandler(
  message: Message,
  server: String
): Result<Unit> = runCatching {
  val senderName = with(message.profile) { nick ?: username ?: Base64.encodeToString(id) }
  val msgList = message.chain.filterIsInstance<MessageType.Text>()
  msgList.forEach {
    if (CONFIG.enableReceiveFilter && !it.content.startsWith(CONFIG.receiveFilter)) return@forEach
    val text = renderMessage(senderName, it.content)
    Bukkit.broadcastMessage(text)
  }
}
