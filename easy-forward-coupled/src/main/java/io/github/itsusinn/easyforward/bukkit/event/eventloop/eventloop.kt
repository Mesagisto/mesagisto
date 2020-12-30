package io.github.itsusinn.easyforward.bukkit.event.eventloop

import io.github.itsusinn.easyforward.bukkit.event.Event
import io.github.itsusinn.mirai.desktop.event.*
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.MessageCodec
import kotlin.reflect.jvm.jvmName

object EventLoop{
   private val vertx = Vertx.vertx()
   val eventBus: EventBus = vertx.eventBus()
   init {
      eventBus.registerLocalCodec()
   }
}

fun EventBus.registerLocalCodec(){

}

inline fun <reified T> LocalMessageCodec(): MessageCodec<T, T> where T: Event
        = object : MessageCodec<T, T> {
   val name = "${T::class.jvmName}Codec"
   override fun transform(s: T): T = s
   override fun name(): String = name
   override fun systemCodecID(): Byte = -1
   override fun encodeToWire(buffer: Buffer, s: T) {
      throw NotImplementedError("No need to impl")
   }
   override fun decodeFromWire(pos: Int, buffer: Buffer): T {
      throw NotImplementedError("No need to impl")
   }
}

