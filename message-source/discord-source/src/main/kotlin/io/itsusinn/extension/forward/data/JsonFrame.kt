package io.itsusinn.extension.forward.data

import com.fasterxml.jackson.annotation.JsonTypeInfo

interface Frame

data class FrameData(
   val code: Int,
   @JsonTypeInfo(
      use = JsonTypeInfo.Id.CLASS,
      include = JsonTypeInfo.As.PROPERTY,
      property = "_type"
   )
   val data: Frame,
)
