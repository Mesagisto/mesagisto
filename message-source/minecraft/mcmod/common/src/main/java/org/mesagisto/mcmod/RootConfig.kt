package org.mesagisto.mcmod

import org.mesagisto.client.Server
import java.util.*
import java.util.concurrent.ConcurrentHashMap

data class RootConfig(
  val enable: Boolean = false,
  val channel: String = "your-channel",
  val target: String = "target-name",
  val centers: ConcurrentHashMap<String, String> = ConcurrentHashMap<String, String>(1).apply { put("mesagisto", "wss://builtin") },
  val cipher: CipherConfig = CipherConfig()
) {
  fun roomId(): UUID {
    return Server.roomId(channel)
  }
}

data class CipherConfig(
  val key: String = "default-key"
)
