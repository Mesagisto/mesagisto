package io.itsusinn.extension.forward.data

abstract class Event : Pack

data class TestEvent(
   val content: String,
) : Event()
