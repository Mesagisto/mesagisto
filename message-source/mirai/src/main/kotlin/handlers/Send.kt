package org.meowcat.mesagisto.mirai.handlers // ktlint-disable filename

import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.* // ktlint-disable no-wildcard-imports
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import org.meowcat.mesagisto.mirai.*
import org.meowcat.mesagisto.mirai.Plugin.Config
import org.mesagisto.client.* // ktlint-disable no-wildcard-imports
import org.mesagisto.client.data.* // ktlint-disable no-wildcard-imports
import org.mesagisto.client.data.Message
import org.mesagisto.client.utils.left

suspend fun sendHandler(
  event: GroupMessageEvent
): Unit = with(event) {
  // 获取目标群聊的信使地址,若不存在则返回
  val roomAddress = Config.bindings[subject.id] ?: return
  // 黑名单检查
  if (Config.perm.strict && sender.id in Config.blacklist) return

  // 保存聊天记录用于引用回复
  val msgId = message.ids.first()
  MiraiDb.putMsgSource(event.source)
  // 构建消息
  Db.putMsgId(subject.id, msgId, msgId)
  var replyId: ByteArray? = null
  val chain = message.mapNotNull map@{
    when (it) {
      is PlainText -> {
        // 有时mirai会出现没有内容的消息,过滤
        if (!it.isContentEmpty()) {
          MessageType.Text(it.content)
        } else null
      }
      is Image -> {
        val imageID = it.imageId.toByteArray()
        Res.storePhotoId(imageID)
        Plugin.launch {
          Res.fileByUrl(imageID, it.queryUrl()).getOrThrow()
        }
        if (Config.switch.allAsSticker) {
          MessageType.Sticker(imageID)
        } else {
          MessageType.Image(imageID)
        }
      }
      is FlashImage -> {
        val image = it.image
        val imageID = image.imageId.toByteArray()
        Res.storePhotoId(imageID)
        Plugin.launch {
          Res.fileByUrl(imageID, image.queryUrl()).getOrThrow()
        }
        if (Config.switch.allAsSticker) {
          MessageType.Sticker(imageID)
        } else {
          MessageType.Image(imageID)
        }
      }
      is QuoteReply -> {
        val localId = it.source.ids.first()
        replyId = Db.getMsgIdByLocal(subject.id.toByteArray(), localId.toByteArray())
        null
      }
      is At -> {
        if (Bot.getInstanceOrNull(it.target) == null) {
          MessageType.Text(it.getDisplay(subject))
        } else {
          null
        }
      }
      is Face -> {
        MessageType.Text(it.contentToString())
      }
      // 拦截MessageSource与MessageOrigin等，防止出现莫名其妙的UnsupportedMessage
      is MessageMetadata -> null // 其实现，如QuoteReply应在此处以上添加
      else -> MessageType.Text("Unsupported message")
    }
  }

  // 非空检查
  if (chain.isEmpty()) return@with
  val message = Message(
    profile = Profile(
      sender.id.toByteArray(),
      sender.id.toString(),
      // TODO Unicode空白控制符
      sender.nameCardOrNick.ifEmpty { null }
    ),
    id = msgId.toByteArray(),
    reply = replyId,
    chain = chain,
    from = subject.id.toByteArray()
  )
  val roomId = Server.roomId(roomAddress)
  val packet = Packet.new(
    roomId,
    message.left()
  )
  Server.send(packet, "mesagisto")
}
