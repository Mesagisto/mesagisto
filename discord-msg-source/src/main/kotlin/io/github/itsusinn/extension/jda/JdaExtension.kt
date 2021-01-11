package io.github.itsusinn.extension.jda

import io.github.itsusinn.extension.okhttp.proxy
import io.github.itsusinn.extension.okhttp.proxyAuth
import io.github.itsusinn.extension.thread.SingleThreadCoroutineScope
import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder

import net.dv8tion.jda.api.events.ReadyEvent
import okhttp3.OkHttpClient
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DiscordBotClient private constructor(
   val jda: JDA
){

   companion object Manager: SingleThreadCoroutineScope("discord") {
      private val logger = KotlinLogging.logger {  }
      suspend fun create(
         token:String,
         hostname:String? = null,
         port:Int? = null,
         username:String? = null,
         password:String? = null,
      ):DiscordBotClient = suspendCoroutine { continuation ->
         try {
            val jdaBuilder = JDABuilder
               .createDefault(token)
               .addEventListeners(Listener)

            //proxy configuration
            val builder = OkHttpClient.Builder()
               .proxy(hostname, port)
               .proxyAuth(username, password)
            jdaBuilder.setHttpClientBuilder(builder)
            logger.debug { "listen ready event" }
            //async,directly return
            listenEventOnce<ReadyEvent>("ready-$token"){
               if (it.jda.token==token){
                  logger.debug { "ready event $token" }
                  continuation.resume(DiscordBotClient(it.jda))
                  return@listenEventOnce true
               }
               return@listenEventOnce false
            }
            logger.debug { "start build jda" }
            jdaBuilder.build()
         }catch (e:Throwable){
            continuation.resumeWithException(e)
         }
      }
   }
}