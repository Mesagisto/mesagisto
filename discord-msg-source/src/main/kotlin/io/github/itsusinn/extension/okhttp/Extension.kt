package io.github.itsusinn.extension.okhttp

import okhttp3.Credentials
import okhttp3.OkHttpClient.Builder
import java.net.InetSocketAddress
import java.net.Proxy

fun Builder.proxy(hostname:String?,port:Int?):Builder{
   if (hostname == null || port == null) return this
   proxy(
      Proxy(Proxy.Type.HTTP, InetSocketAddress(hostname, port))
   )
   return this
}

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