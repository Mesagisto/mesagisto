package itsusinn.extension.vertx.websocket

import itsusinn.extension.base64.base64
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.http.WebSocketFrame
val pingText = "[ping]"
val pingBuffer = Buffer.buffer(pingText)
val pingFrame = WebSocketFrame.textFrame(pingText,true)
