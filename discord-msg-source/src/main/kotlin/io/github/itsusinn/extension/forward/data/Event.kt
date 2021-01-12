package io.github.itsusinn.extension.forward.data

import com.fasterxml.jackson.annotation.JsonTypeInfo
import net.dv8tion.jda.api.utils.ChunkingFilter.include

abstract class Event: Frame

data class TestEvent(
   val content: String,
): Event()