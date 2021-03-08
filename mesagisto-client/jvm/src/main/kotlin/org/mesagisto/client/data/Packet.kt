@file:Suppress("ArrayInDataClass", "MemberVisibilityCanBePrivate", "ktlint:standard:no-wildcard-imports")

package org.mesagisto.client.data

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.jetbrains.annotations.TestOnly
import org.mesagisto.client.Cbor
import org.mesagisto.client.Cipher
import org.mesagisto.client.toHex
import java.util.*

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "t")
@JsonSubTypes(
  Type(Message::class, name = "m"),
  Type(Event::class, name = "e"),
)
sealed class MessageOrEvent

data class Packet constructor(
  val content: ByteArray = ByteArray(0),
  val rid: UUID = UUID.randomUUID(),
) {
  companion object {
    fun new(
      roomId: UUID,
      data: MessageOrEvent,
    ): Packet {
      val bytes = Cipher.encrypt(Cbor.encodeToByteArray(data))
      return Packet(
        content = bytes,
        rid = roomId,
      )
    }
  }
}

@TestOnly
fun test() {
  val message = Message(chain = arrayListOf(MessageType.Text("aab")))
  val bytes = Cbor.encodeToByteArray(message)
  println(bytes.toHex())
  val a = Cbor.decodeFromByteArray<MessageOrEvent>(bytes)
  println(a)
}
