package org.meowcat.mesagisto.kato.handlers

import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.meowcat.mesagisto.kato.Plugin.CONFIG
import org.meowcat.mesagisto.kato.Plugin.DATA
import org.meowcat.mesagisto.kato.Template
import org.meowcat.mesagisto.kato.asBytes
import org.meowcat.mesagisto.kato.stripColor
import org.mesagisto.client.Server
import org.mesagisto.client.data.Message
import org.mesagisto.client.data.MessageType
import org.mesagisto.client.data.Packet
import org.mesagisto.client.data.Profile
import org.mesagisto.client.toByteArray
import org.mesagisto.client.utils.left


suspend fun send(
  event: AsyncPlayerChatEvent
) {
  val roomId = CONFIG.roomId()
  val msgId = DATA.idCounter.getAndIncrement()
  if (CONFIG.enableSendFilter && !event.message.stripColor().startsWith(CONFIG.sendFilter)) return
  val chain = listOf<MessageType>(
    MessageType.Text(event.message.stripColor())
  )
  val sender = event.player
  val message = Message(
    profile = Profile(
      sender.uniqueId.asBytes(),
      sender.name.stripColor(),
      sender.playerListName.stripColor()
    ),
    id = msgId.toByteArray(),
    chain = chain,
    from = CONFIG.target.toByteArray()
  )
  val packet = Packet.new(roomId, message.left())
  Server.send(packet, "mesagisto")
}
suspend fun sendPlayerJoin(event: PlayerJoinEvent) {
  val roomId = CONFIG.roomId()
  val servername = CONFIG.serverName
  val msgId = DATA.idCounter.getAndIncrement()
  val chain = listOf<MessageType>(
    MessageType.Text(Template.renderJoin(event.player.playerListName.stripColor()))
  )
  val message = Message(
    profile = Profile(
      0L.toByteArray(),
      servername,
      null
    ),
    id = msgId.toByteArray(),
    chain = chain,
    from = CONFIG.target.toByteArray()
  )
  val packet = Packet.new(roomId, message.left())
  Server.send(packet, "mesagisto")
}
suspend fun sendPlayerLeave(event: PlayerQuitEvent) {
  val roomId = CONFIG.roomId()
  val servername = CONFIG.serverName
  val msgId = DATA.idCounter.getAndIncrement()
  val chain = listOf<MessageType>(
    MessageType.Text(Template.renderLeave(event.player.playerListName.stripColor()))
  )
  val message = Message(
    profile = Profile(
      0L.toByteArray(),
      servername,
      null
    ),
    id = msgId.toByteArray(),
    chain = chain,
    from = CONFIG.target.toByteArray()
  )
  val packet = Packet.new(roomId, message.left())
  Server.send(packet, "mesagisto")
}
suspend fun sendPlayerDeath(event: PlayerDeathEvent) {
  val roomId = CONFIG.roomId()
  val servername = CONFIG.serverName
  val msgId = DATA.idCounter.getAndIncrement()
  val chain = listOf<MessageType>(
    MessageType.Text(Template.renderDeath(event.entity.playerListName, event.deathMessage.stripColor()))
  )
  val message = Message(
    profile = Profile(
      0L.toByteArray(),
      servername,
      null
    ),
    id = msgId.toByteArray(),
    chain = chain,
    from = CONFIG.target.toByteArray()
  )
  val packet = Packet.new(roomId, message.left())
  Server.send(packet, "mesagisto")
}
