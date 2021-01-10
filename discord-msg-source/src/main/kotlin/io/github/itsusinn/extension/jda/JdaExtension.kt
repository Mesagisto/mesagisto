package io.github.itsusinn.extension.jda

import io.github.itsusinn.extension.log.logger
import io.github.itsusinn.extension.thread.SingleThreadLoop
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.JDABuilder

import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.hooks.EventListener
import okhttp3.Credentials
import okhttp3.OkHttpClient
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DiscordBotClient private constructor(
   val jda: JDABuilder
){

   companion object Manager: SingleThreadLoop() {
      suspend fun create(
         token:String,
         hostname:String? = null,
         port:Int? = null,
         username:String? = null,
         password:String? = null,
      ):DiscordBotClient = suspendCoroutine { continuation ->
         try {

            val jdaBuilder = JDABuilder.createDefault(token).addEventListeners(Listener)

            //proxy configuration
            if (hostname != null && port != null) {
               val builder = OkHttpClient.Builder()
               builder.proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress(hostname, port)))
               if (username != null && password != null) {
                  //proxy authenticate
                  val credential = Credentials.basic(username, password)
                  builder.proxyAuthenticator { _, response ->
                     response.request().newBuilder()
                        .header("Proxy-Authorization", credential)
                        .build()
                  }
               }
               jdaBuilder.setHttpClientBuilder(builder)
            }

            //async
            listenEventOnce<ReadyEvent>("ready"){
               continuation.resume(DiscordBotClient(jdaBuilder))
            }
            jdaBuilder.build()
         }catch (e:Throwable){
            Listener.unregister("ready")
            continuation.resumeWithException(e)
         }
      }
   }
}