package io.github.itsusinn.extension.base64

import java.util.*

private val decoder = Base64.getUrlDecoder()
private val encoder = Base64.getUrlEncoder()
val String.base64
   get() = encoder.encodeToString(this.toByteArray())
val String.debase64
   get() = String(decoder.decode(this))