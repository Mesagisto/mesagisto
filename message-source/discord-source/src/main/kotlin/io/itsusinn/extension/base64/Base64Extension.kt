package io.itsusinn.extension.base64

import java.util.* // ktlint-disable no-wildcard-imports

private val decoder = Base64.getUrlDecoder()
private val encoder = Base64.getUrlEncoder()
val String.base64
   get() = encoder.encodeToString(this.toByteArray())
val String.debase64
   get() = decoder.decode(this)
