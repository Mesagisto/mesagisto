package io.github.itsusinn.extension.okhttp

import mu.KotlinLogging
import okhttp3.Credentials
import okhttp3.OkHttpClient.Builder
import java.net.InetSocketAddress
import java.net.Proxy

/**
 * The proxy will not be set if one of the parameters is empty
 */
private val logger = KotlinLogging.logger {  }
fun Builder.proxy(hostname:String?,port:Int?):Builder{
   if (hostname == null || port == null) return this
   proxy(
      kotlin.runCatching {
         logger.info { "using proxy $hostname $port" }
         Proxy(Proxy.Type.HTTP, InetSocketAddress(hostname, port))
      }.getOrElse {
         logger.warn { "proxy create failed ${it.message} \n" + it.stackTrace }
         Proxy.NO_PROXY
      }
   )
   return this
}
/**
 * The proxy auth will not be set if one of the parameters is empty
 */
fun Builder.proxyAuth(username:String?,password:String?):Builder{
   if (username != null && password != null) {
      //proxy authenticate
      val credential = Credentials.basic(username, password)
      proxyAuthenticator { _, response ->
         response.request().newBuilder()
            .header("Proxy-Authorization", credential)
            .build()
      }
   }
   return this
}