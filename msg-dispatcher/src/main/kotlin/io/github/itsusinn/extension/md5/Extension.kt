@file:Suppress("EXPERIMENTAL_API_USAGE")
package io.github.itsusinn.extension.md5

import io.github.itsusinn.extension.base64.base64
import java.security.MessageDigest

val String.md5
   get() = run {
      val digest = MessageDigest.getInstance("MD5")
      digest.update(this.toByteArray())
      digest.digest()
   }

