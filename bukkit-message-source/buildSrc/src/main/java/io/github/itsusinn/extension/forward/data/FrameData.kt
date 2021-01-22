package io.github.itsusinn.extension.forward.data

import com.fasterxml.jackson.annotation.JsonTypeInfo

interface Pack

data class FrameData(
   val code:Int,
   @JsonTypeInfo(
      use = JsonTypeInfo.Id.CLASS,
      include = JsonTypeInfo.As.PROPERTY,
      property = "_type"
   )
   val data:Pack,
)