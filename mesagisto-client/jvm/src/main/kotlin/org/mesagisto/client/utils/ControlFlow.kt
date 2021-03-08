package org.mesagisto.client.utils

import org.jetbrains.annotations.TestOnly

sealed class ControlFlow<out B, out C> {
  data class Break<out B>(val value: B) : ControlFlow<B, Nothing>()
  data class Continue<out C>(val value: C) : ControlFlow<Nothing, C>()
}

@TestOnly
fun test(): ControlFlow<Long, Unit> {
  return ControlFlow.Break(1L)
  // return ControlFlow.Continue(11L)
}
