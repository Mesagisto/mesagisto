package org.mesagisto.mcmod.handlers

import org.mesagisto.client.Base64
import org.mesagisto.client.Logger
import org.mesagisto.client.Server
import org.mesagisto.client.data.Message
import org.mesagisto.client.data.MessageType
import org.mesagisto.client.data.Packet
import org.mesagisto.client.utils.ControlFlow
import org.mesagisto.client.utils.Either
import org.mesagisto.client.withCatch
import org.mesagisto.mcmod.ModEntry
import org.mesagisto.mcmod.ModEntry.CONFIG
import org.mesagisto.mcmod.api.ChatImpl

object Receive {
  suspend fun recover() {
    add(CONFIG.channel)
  }
  suspend fun add(roomAddress: String) {
    val roomId = Server.roomId(roomAddress)
    Server.sub(roomId, "mesagisto")
  }
  suspend fun packetHandler(pkt: Packet): Result<ControlFlow<Packet, Unit>> = withCatch(ModEntry.coroutineContext) fn@{
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
              msgHandler(it.value).onFailure { e -> Logger.error(e) }
            }
          }
          is Either.Right -> return@fn ControlFlow.Break(pkt)
        }
      }
    return@fn ControlFlow.Continue(Unit)
  }
}

fun msgHandler(
  message: Message
): Result<Unit> = runCatching fn@{
  val senderName = with(message.profile) { nick ?: username ?: Base64.encodeToString(id) }
  val msgList = message.chain.filterIsInstance<MessageType.Text>()
  msgList.forEach {
    val text = "<$senderName> ${it.content}"
    ChatImpl.broadcastMessage(text)
  }
}
