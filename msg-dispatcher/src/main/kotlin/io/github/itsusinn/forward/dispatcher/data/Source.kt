package io.github.itsusinn.forward.dispatcher.data

import io.vertx.core.http.ServerWebSocket

data class Source(
   val identifier:String,
   val ws: ServerWebSocket
)