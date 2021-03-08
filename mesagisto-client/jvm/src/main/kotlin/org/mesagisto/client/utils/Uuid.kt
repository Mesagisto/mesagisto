@file:Suppress("NOTHING_TO_INLINE", "ktlint:standard:filename")

package org.mesagisto.client.utils

import com.fasterxml.uuid.Generators
import com.fasterxml.uuid.impl.NameBasedGenerator
import java.util.UUID

object UUIDv5 {
  private val NAMESPACE_MSGIST: UUID = UUID.fromString("179e3449-c41f-4a57-a763-59a787efaa52")
  val generator: ThreadLocal<NameBasedGenerator> =
    ThreadLocal.withInitial {
      Generators.nameBasedGenerator(NAMESPACE_MSGIST)
    }

  inline fun fromString(name: String): UUID = generator.get().generate(name)

  inline fun fromBytes(name: ByteArray): UUID = generator.get().generate(name)
}
