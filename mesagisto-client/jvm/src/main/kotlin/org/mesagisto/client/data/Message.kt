@file:Suppress("ArrayInDataClass", "unused")

package org.mesagisto.client.data

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo

data class Message(
  val profile: Profile = Profile(),
  val id: ByteArray = ByteArray(0),
  val from: ByteArray = ByteArray(0),
  val reply: ByteArray? = null,
  val chain: List<MessageType> = emptyList(),
) : MessageOrEvent()

data class Profile(
  val id: ByteArray = ByteArray(0),
  val username: String? = null,
  val nick: String? = null,
)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "t")
@JsonSubTypes(
  Type(MessageType.Text::class, name = "text"),
  Type(MessageType.Image::class, name = "image"),
  Type(MessageType.Sticker::class, name = "sticker"),
)
sealed class MessageType {
  data class Text(
    val content: String = "",
  ) : MessageType()

  data class Image(
    val id: ByteArray = ByteArray(0),
    val url: String? = null,
  ) : MessageType()

  data class Sticker(
    // unique id supplied by platform
    val id: ByteArray = ByteArray(0),
    val url: String? = null,
  ) : MessageType()
}
