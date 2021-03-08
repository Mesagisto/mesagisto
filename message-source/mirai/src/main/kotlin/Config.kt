package org.meowcat.mesagisto.mirai

import net.mamoe.mirai.contact.Group
import org.mesagisto.client.Server
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

data class RootConfig(
  val cipher: CipherConfig = CipherConfig(),
  val switch: SwitchConfig = SwitchConfig(),
  val proxy: ProxyConfig = ProxyConfig(),
  val centers: ConcurrentHashMap<String, String> = ConcurrentHashMap<String, String>(1),
  val override_center: String = "",

  val perm: PermConfig = PermConfig(),
  val bindings: ConcurrentHashMap<Long, String> = ConcurrentHashMap(),
  val blacklist: ConcurrentLinkedQueue<Long> = ConcurrentLinkedQueue(),

  val disable_group: ConcurrentLinkedQueue<Long> = ConcurrentLinkedQueue(),
  val disable_channel: ConcurrentLinkedQueue<String> = ConcurrentLinkedQueue()
) {

  fun mapper(target: Long): String? = bindings[target]
  fun mapper(target: Group): String? = bindings[target.id]
  fun migrate() {
    centers["mesagisto"] = "wss://builtin"
  }
  fun roomAddress(target: Long): String? = bindings[target]

  fun roomId(target: Long): UUID? {
    val roomAddress = roomAddress(target) ?: return null
    return Server.roomId(roomAddress)
  }

  fun targetId(roomId: UUID): List<Long>? {
    val roomAddress = Server.roomMap.firstNotNullOfOrNull {
      if (it.value == roomId) it.key else null
    } ?: return null
    val targets = bindings.mapNotNull {
      if (it.value == roomAddress) it.key else null
    }
    return targets
  }
}

@Suppress("DEPRECATION")
val RootConfig.disableGroup
  get() = disable_group

@Suppress("DEPRECATION")
val RootConfig.disableChannel
  get() = disable_channel

data class PermConfig(
  val strict: Boolean = false,
  val users: ConcurrentLinkedQueue<Long> = ConcurrentLinkedQueue()
)

data class ProxyConfig(
  val enable: Boolean = false,
  val address: String = "http://127.0.0.1:7890"
)

data class CipherConfig(
  val key: String = "default"
)

data class SwitchConfig(
  val nudge: Boolean = true,
  val allAsSticker: Boolean = true
)
