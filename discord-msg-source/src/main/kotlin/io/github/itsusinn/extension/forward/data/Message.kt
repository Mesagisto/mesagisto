package io.github.itsusinn.extension.forward.data

import com.fasterxml.jackson.annotation.JsonTypeInfo

abstract class Message: Frame

data class TestMessage(val content:String):Message()
