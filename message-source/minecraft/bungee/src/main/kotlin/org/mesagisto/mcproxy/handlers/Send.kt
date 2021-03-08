package org.mesagisto.mcproxy.handlers

import net.md_5.bungee.api.event.ChatEvent
import org.mesagisto.client.Server
import org.mesagisto.client.data.Message
import org.mesagisto.client.data.MessageType
import org.mesagisto.client.data.Packet
import org.mesagisto.client.data.Profile
import org.mesagisto.client.toByteArray
import org.mesagisto.client.utils.left
import org.mesagisto.mcproxy.MultiServer
import org.mesagisto.mcproxy.Plugin.CONFIG
import org.mesagisto.mcproxy.asBytes
import java.util.concurrent.atomic.AtomicInteger

suspend fun send(
  event: ChatEvent
) {
  val target = MultiServer.getServerName(event.receiver) ?: return
  val sender = MultiServer.getPlayer(event.sender) ?: return

  val roomAddress = CONFIG.bindings[target] ?: return
  val msgId = CONFIG.idCounter
    .getOrPut(target) { AtomicInteger(0) }
    .getAndIncrement()

  val message = Message(
    profile = Profile(
      sender.uniqueId.asBytes(),
      sender.name,
      sender.displayName
    ),
    id = msgId.toByteArray(),
    chain = listOf<MessageType>(
      MessageType.Text(event.message)
    ),
    from = target.toByteArray()
  )
  val roomId = Server.roomId(roomAddress)
  val packet = Packet.new(
    roomId,
    message.left()
  )
  Server.send(packet, "mesagisto")
}
