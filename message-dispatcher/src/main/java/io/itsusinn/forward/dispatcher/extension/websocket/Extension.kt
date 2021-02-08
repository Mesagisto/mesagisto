package itsusinn.extension.vertx.websocket

import io.vertx.core.buffer.Buffer
import io.vertx.core.http.WebSocketFrame
val pingText = "[ping]"
val pingBuffer = Buffer.buffer(pingText)
val pingFrame = WebSocketFrame.textFrame(pingText, true)
