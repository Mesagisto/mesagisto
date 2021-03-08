@file:Suppress("MemberVisibilityCanBePrivate", "ktlint:standard:no-wildcard-imports")

package org.mesagisto.client

import io.nats.client.Connection
import io.nats.client.Dispatcher
import io.nats.client.Nats
import io.nats.client.Subscription
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.future.await
import org.mesagisto.client.data.MessageOrEvent
import org.mesagisto.client.data.Packet
import org.mesagisto.client.utils.ControlFlow
import org.mesagisto.client.utils.UUIDv5
import java.io.Closeable
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

typealias PackHandler = suspend (Packet) -> Result<ControlFlow<Packet, Unit>>
typealias ServerName = String

object Server : Closeable, CoroutineScope {
  private val remoteEndpoints = ConcurrentHashMap<String, NatsServer>()
  lateinit var packetHandler: PackHandler
  lateinit var remotes: Map<String, String>

  val roomMap = ConcurrentHashMap<String, UUID>()
  val pktChannel = Channel<Packet>()

  init {
    poll()
  }

  fun poll() =
    launch {
      while (true) {
        val pkt = pktChannel.receive()
        launch {
          val res = packetHandler.invoke(pkt)
        }
      }
    }

  suspend fun init(
    remotes: MutableMap<String, String>,
    overrideCenter: String,
  ) = withCatch(Dispatchers.Default) {
    this@Server.remotes = remotes
    if (overrideCenter.isNotBlank()) {
      remotes["mesagisto"] = overrideCenter
    } else {
      remotes["mesagisto"] = "mesagisto.itsusinn.site"
    }
    val endpoints =
      remotes.map {
        val serverName = it.key
        val serverAddress = it.value
        async {
          Logger.info { "Connecting to $serverName $serverAddress" }
          val conn = Nats.connect(serverAddress)
          val dispatcher = conn.createDispatcher()
          serverName to NatsServer(conn, dispatcher)
        }
      }.awaitAll()
    remoteEndpoints.putAll(endpoints)
  }

  fun roomId(roomAddress: String): UUID =
    roomMap.getOrPut(roomAddress) {
      val uniqueAddress = Cipher.uniqueAddress(roomAddress)
      UUIDv5.fromString(uniqueAddress)
    }

  override fun close() {
    for (endpoint in remoteEndpoints) {
      runCatching {
        endpoint.value.close()
      }.onFailure {
        it.printStackTrace()
      }
    }
  }

  suspend fun send(
    content: Packet,
    serverName: String,
  ) = withContext(Dispatchers.Default) fn@{
    val payload = Cbor.encodeToByteArray(content)
    val remote =
      remoteEndpoints[serverName] ?: run {
        Logger.warn { "Relay Server connection hasn't been established yet. Message received will be abandoned" }
        return@fn
      }
    remote.publish(content.rid.toString(), payload)
  }

  val subs = ConcurrentHashMap<ServerName, ConcurrentHashMap<UUID, SubCounter>>()

  suspend fun sub(
    room: UUID,
    serverName: String,
  ) {
    Logger.debug { "Sub on $serverName $room" }
    val remote =
      remoteEndpoints[serverName] ?: run {
        Logger.warn { "Relay Server connection hasn't been established yet. Message received will be abandoned" }
        return
      }

    val entry =
      subs.getOrPut(serverName) {
        ConcurrentHashMap()
      }.getOrPut(room) {
        SubCounter(
          AtomicInteger(0),
          remote.dispatcher.subscribe(room.toString()) {
            runBlocking {
              pktChannel.send(Packet(it.data, room))
            }
          },
        )
      }
    entry.counter.incrementAndGet()
  }

  suspend fun unsub(
    room: UUID,
    serverName: String,
  ) {
    val entry = subs.getOrPut(serverName) { ConcurrentHashMap() }[room] ?: return
    if (entry.counter.decrementAndGet() < 1) {
      entry.sub.unsubscribe()
    }
  }

  suspend fun request(
    pkt: Packet,
    serverName: String,
  ) = withCatch(Dispatchers.Default) fn@{
    val remote =
      remoteEndpoints[serverName] ?: run {
        Logger.warn { "Relay Server connection hasn't been established yet. Message received will be abandoned" }
        return@fn null
      }

    // TODO TIMEOUT
    val reply = remote.request(pkt.rid.toString(), pkt.content).orTimeout(1, TimeUnit.SECONDS).await()
    Cbor.decodeFromByteArray<MessageOrEvent>(Cipher.decrypt(reply.data))
  }

  suspend fun respond(
    pkt: Packet,
    serverName: String,
    inbox: NatsMessage,
  ) = withCatch(Dispatchers.Default) fn@{
    val remote =
      remoteEndpoints[serverName] ?: run {
        Logger.warn { "Relay Server connection hasn't been established yet. Message received will be abandoned" }
        return@fn
      }
    remote.publish(inbox.replyTo, pkt.content)
  }

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.Default
}

data class SubCounter(val counter: AtomicInteger, val sub: Subscription)

data class NatsServer(val connection: Connection, val dispatcher: Dispatcher) : Connection by connection

typealias NatsMessage = io.nats.client.Message
