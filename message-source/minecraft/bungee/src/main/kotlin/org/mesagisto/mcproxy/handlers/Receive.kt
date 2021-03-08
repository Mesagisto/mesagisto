package org.mesagisto.mcproxy.handlers

import com.github.jknack.handlebars.Context
import org.mesagisto.client.Base64
import org.mesagisto.client.Logger
import org.mesagisto.client.Server
import org.mesagisto.client.data.Message
import org.mesagisto.client.data.MessageType
import org.mesagisto.client.data.Packet
import org.mesagisto.client.data.roomId
import org.mesagisto.client.utils.ControlFlow
import org.mesagisto.client.utils.Either
import org.mesagisto.client.withCatch
import org.mesagisto.mcproxy.MultiServer
import org.mesagisto.mcproxy.Plugin
import org.mesagisto.mcproxy.Plugin.CONFIG
import org.mesagisto.mcproxy.Template

object Receive {
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
            for (target in CONFIG.targetId(pkt.roomId) ?: return@fn ControlFlow.Break(pkt)) {
              if (!it.value.from.contentEquals(target.toByteArray())) {
                msgHandler(it.value, target).onFailure { e -> Logger.error(e) }
              }
            }
          }
          is Either.Right -> return@fn ControlFlow.Break(pkt)
        }
      }
    return@fn ControlFlow.Continue(Unit)
  }
  suspend fun recover() {
    for (roomAddress in CONFIG.bindings.values) {
      add(roomAddress)
    }
  }
  suspend fun add(roomAddress: String) {
    val roomId = Server.roomId(roomAddress)
    Server.sub(roomId, "mesagisto")
  }
  suspend fun change(before: String, after: String) {
    del(before)
    add(after)
  }
  suspend fun del(roomAddress: String) {
    val roomId = Server.roomId(roomAddress)
    // FIXME 同侧互通 考虑当接受到不属于任何群聊的数据包时才unsub
    Server.unsub(roomId, "mesagisto")
  }
}
private fun msgHandler(
  message: Message,
  target: String
): Result<Unit> = runCatching fn@{
  val senderName = with(message.profile) { nick ?: username ?: Base64.encodeToString(id) }
  val msgList = message.chain.filterIsInstance<MessageType.Text>()
  msgList.forEach {
    val text = renderMessage(senderName, it.content)
    MultiServer.broadcastOn(target, text)
  }
}

private fun renderMessage(sender: String, content: String): String {
  val module = HashMap<String, String>(2)
  module.apply {
    put("sender", sender)
    put("content", content)
  }
  val context = Context.newContext(module)
  return Template.apply("message", context)
}
