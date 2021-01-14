package io.github.itsusinn.extension.forward.data

import com.fasterxml.jackson.annotation.JsonTypeInfo

abstract class Event: Frame

data class TestEvent(
   val content: String,
): Event()