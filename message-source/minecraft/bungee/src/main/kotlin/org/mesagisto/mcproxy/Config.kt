package org.mesagisto.mcproxy

import com.fasterxml.jackson.annotation.JsonAlias
import org.mesagisto.client.Server
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

data class RootConfig(
  val enable: Boolean = false,
  val bindings: ConcurrentHashMap<String, String> = ConcurrentHashMap(),
  val cipher: CipherConfig = CipherConfig(),
  @JsonAlias("id_counter")
  val idCounter: MutableMap<String, AtomicInteger> = ConcurrentHashMap(),
  val centers: ConcurrentHashMap<String, String> = ConcurrentHashMap<String, String>(1).apply { put("mesagisto", "wss://center.mesagisto.org") },
  val template: TemplateConfig = TemplateConfig()
) {
  fun targetId(roomId: UUID): List<String>? {
    val roomAddress = Server.roomMap.firstNotNullOfOrNull {
      if (it.value == roomId) it.key else null
    } ?: return null
    val targets = bindings.mapNotNull {
      if (it.value == roomAddress) it.key else null
    }
    return targets
  }
}

data class CipherConfig(
  val key: String = "your-key"
)

data class TemplateConfig(
  val message: String = "§7<{{sender}}> {{content}}"
)
