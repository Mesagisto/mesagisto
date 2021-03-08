package org.mesagisto.mcmod.handlers

import org.mesagisto.client.Server
import org.mesagisto.client.data.* // ktlint-disable no-wildcard-imports
import org.mesagisto.client.toByteArray
import org.mesagisto.client.utils.left
import org.mesagisto.mcmod.ModEntry.CONFIG
import org.mesagisto.mcmod.ModEntry.DATA

suspend fun send(
  sender: String,
  content: String
) {
  val roomId = CONFIG.roomId()
  val msgId = DATA.idCounter
  val chain = listOf<MessageType>(
    MessageType.Text(content)
  )
  val message = Message(
    profile = Profile(
      ByteArray(0),
      sender,
      null
    ),
    id = msgId.getAndIncrement().toByteArray(),
    chain = chain,
    from = CONFIG.target.toByteArray()
  )
  val packet = Packet.new(roomId, message.left())
  Server.send(packet, "mesagisto")
}
