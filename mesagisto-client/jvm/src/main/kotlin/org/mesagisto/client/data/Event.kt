@file:Suppress("ArrayInDataClass", "unused")

package org.mesagisto.client.data

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "t")
@JsonSubTypes(
  Type(Event.RequestImage::class, name = "request_image"),
  Type(Event.RespondImage::class, name = "respond_image"),
)
sealed class Event : MessageOrEvent() {
  data class RequestImage constructor(
    val id: ByteArray = ByteArray(0),
  ) : Event()

  data class RespondImage constructor(
    val id: ByteArray = ByteArray(0),
    val url: String = "",
  ) : Event()
}
