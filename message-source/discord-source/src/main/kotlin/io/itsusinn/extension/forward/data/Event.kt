package io.itsusinn.extension.forward.data

abstract class Event : Frame

data class TestEvent(
   val content: String,
) : Event()
