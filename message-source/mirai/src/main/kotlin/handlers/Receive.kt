package org.meowcat.mesagisto.mirai.handlers

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.joinAll
import net.mamoe.mirai.Mirai
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.QuoteReply
import net.mamoe.mirai.message.data.toMessageChain
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.meowcat.mesagisto.mirai.* // ktlint-disable no-wildcard-imports
import org.meowcat.mesagisto.mirai.Plugin.Config
import org.mesagisto.client.*
import org.mesagisto.client.data.* // ktlint-disable no-wildcard-imports
import org.mesagisto.client.utils.ControlFlow
import org.mesagisto.client.utils.Either
import org.mesagisto.client.utils.right
import java.nio.file.Path

object Receive {
  suspend fun packetHandler(pkt: Packet): Result<ControlFlow<Packet, Unit>> = withCatch(Plugin.coroutineContext) fn@{
    Logger.info { "Receive msg from ${pkt.roomId}" }
    if (pkt.ctl != null) {
      Logger.info { "Ignoring control packet" }
      return@fn ControlFlow.Continue(Unit)
    }
    val it = pkt.decrypt()
      .onFailure {
        Logger.warn { "Failed to decrypt packet" }
      }
      .getOrThrow()
    when (it) {
      is Either.Left -> {
        for (target in Config.targetId(pkt.roomId) ?: return@fn ControlFlow.Break(pkt)) {
          if (!it.value.from.contentEquals(target.toByteArray())) {
            msgHandler(it.value, target, "mesagisto").onFailure { e -> Logger.error(e) }
          }
        }
      }
      is Either.Right -> {
        when (it.value) {
          is Event.RequestImage -> {
            Logger.info { "received request image" }
            val inbox = pkt.inbox as? Inbox.Request ?: return@fn ControlFlow.Break(pkt)
            val imageRequest = it.value as Event.RequestImage
            val image = Image(imageRequest.id.toString(charset = Charsets.UTF_8))
            val url = image.queryUrl()
            val event = Event.RespondImage(imageRequest.id, url)
            val packet = Packet.new(pkt.roomId, event.right())
            Server.respond(packet, "mesagisto", inbox.id)
          }
          else -> return@fn ControlFlow.Break(pkt)
        }
      }
    }

    ControlFlow.Continue(Unit)
  }
  suspend fun recover() {
    for (roomAddress in Config.bindings.values) {
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
    // TODO 更新Config中的cache
    Server.unsub(roomId, "mesagisto")
  }
}

private suspend fun msgHandler(
  message: Message,
  target: Long,
  server: String
): Result<Unit> = runCatching fn@{
  Mirai.BotFactory
  val group = MiraiBot.getGroup(target) ?: return@fn
  if (Config.disableGroup.contains(group.id)) return@fn
  if (Config.disableChannel.contains(Config.bindings[group.id])) return@fn

  val room = Config.roomAddress(target)
  val roomId = Config.roomId(target) ?: return@fn

  val senderName = with(message.profile) { nick ?: username ?: id.toString() }
  var chain = message.chain.flatMap map@{ it ->
    when (it) {
      is MessageType.Text -> listOf(PlainText("\n${it.content}"))
      is MessageType.Image -> {
        val file = Res.file(it.id, it.url, roomId, server).getOrThrow()
        val image = if (file.isWebp()) {
          Logger.debug { "图片为QQ不支持的WEBP格式,正在转为PNG格式..." }
          var png: Path? = null
          MiraiRes.convert(Base64.encodeToString(it.id), "png", ::convertWebpToPng).onFailure {
            png = null
          }.onSuccess {
            png = it
          }
          png?.toFile()
        } else {
          file.toFile()
        }?.uploadAsImage(group)
        if (image != null) {
          listOf(PlainText("\n"), image)
        } else {
          emptyList()
        }
      }
      is MessageType.Sticker -> {
        val file = Res.file(it.id, it.url, roomId, server).getOrThrow()
        val image = if (file.isWebp()) {
          Logger.debug { "图片为QQ不支持的WEBP格式,正在转为PNG格式..." }
          var png: Path? = null
          MiraiRes.convert(Base64.encodeToString(it.id), "png", ::convertWebpToPng).onFailure {
            png = null
          }.onSuccess {
            png = it
          }
          png?.toFile()
        } else {
          file.toFile()
        }?.uploadAsImage(group)
        if (image != null) {
          listOf(PlainText("\n"), image)
        } else {
          emptyList()
        }
      }
    }
  }.toMessageChain()
  chain = PlainText("$senderName: ").plus(chain)
  run {
    val replyId = message.reply ?: return@run
    val localId = Db.getMsgIdByRemote(target.toByteArray(), replyId)?.toI32() ?: return@run
    val msgSource = MiraiDb.getMsgSource(target, localId) ?: return@run
    chain += QuoteReply(msgSource)
  }
  val receipt = group.sendMessage(chain)
  Db.putMsgId(target, message.id, receipt.source.ids.first())
  MiraiDb.putMsgSource(receipt.source)
}
