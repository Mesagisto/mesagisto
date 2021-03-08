package org.mesagisto.mcmod

import java.util.concurrent.atomic.AtomicInteger

data class RootData(
  val idCounter: AtomicInteger = AtomicInteger(0)
)
