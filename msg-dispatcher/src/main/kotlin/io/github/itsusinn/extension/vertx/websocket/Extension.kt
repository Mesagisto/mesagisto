package io.github.itsusinn.extension.vertx.websocket

import io.github.itsusinn.extension.base64.base64
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.http.WebSocketFrame

val pingBuffer = Buffer.buffer("HeartBeat")
val pingFrame = WebSocketFrame.pingFrame(pingBuffer)
