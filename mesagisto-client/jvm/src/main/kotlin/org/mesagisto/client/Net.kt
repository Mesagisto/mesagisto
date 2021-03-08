@file:Suppress("BlockingMethodInNonBlockingContext")

package org.mesagisto.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.* // ktlint-disable no-wildcard-imports
import java.nio.file.Files
import java.nio.file.Path

object Net {
  private var proxy: Proxy? = null
  fun setProxy(proxyUri: String) {
    Logger.info { "设置代理为 $proxyUri" }
    proxy = run {
      val uri = URI(proxyUri)
      val type = when (uri.scheme) {
        "http" -> Proxy.Type.HTTP
        "socks" -> Proxy.Type.SOCKS
        else -> Proxy.Type.DIRECT
      }
      Proxy(type, InetSocketAddress(uri.host, uri.port))
    }
  }

  suspend fun downloadFile(
    urlStr: String,
    outputFile: Path
  ): Result<Long> = withContext(Dispatchers.IO) {
    runCatching {
      val url = URL(urlStr)
      if (proxy == null) {
        url.openStream().use { Files.copy(it, outputFile) }
      } else {
        url.openConnection(proxy).getInputStream().use { Files.copy(it, outputFile) }
      }
    }
  }
}
