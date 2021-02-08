package itsusinn.forward.test

import io.ktor.client.* // ktlint-disable no-wildcard-imports
import io.ktor.client.engine.* // ktlint-disable no-wildcard-imports
import io.ktor.client.features.websocket.* // ktlint-disable no-wildcard-imports
import io.ktor.http.cio.websocket.* // ktlint-disable no-wildcard-imports
import io.itsusinn.extension.base64.base64
import io.itsusinn.extension.base64.debase64
import io.itsusinn.extension.config.ConfigKeeper
import io.itsusinn.extension.runtime.exit
import io.itsusinn.forward.client.warp
import kotlinx.coroutines.* // ktlint-disable no-wildcard-imports
import mu.KotlinLogging
import java.io.File
import kotlin.coroutines.CoroutineContext

object App : CoroutineScope {
   // Make sure the forward folder exists
   init { File("forward").apply { mkdir() } }

   private val configKeeper = ConfigKeeper
      .create<TestConfigData>(
         defaultConfig, File("forward/test.json")
      )

   private val config = configKeeper.config

   private val logger = KotlinLogging.logger { }

   @JvmStatic fun main(args: Array<String>) = runBlocking<Unit> {
      if (config.startSignal > 1) {
         config.startSignal--
         logger.warn { "Config dont exist,write default config into forward/discord.json" }
         logger.error { "app will exit,please modify config" }
         configKeeper.save()
      } else if (config.startSignal == 1) {
         try {
            start()
         } catch (e: Throwable) {
            logger.error(e) { "start up failed \n" + e.stackTrace }
            exit(1)
         }
      } else {
         logger.warn { "app has been prohibited to start" }
      }
   }

   fun start() = runBlocking<Unit> {
      val client = HttpClient() {
         install(WebSockets)
      }

      val path: String
      val name = "TestCli"
      config.apply {
         path = "/ws?address=${address.base64}&token=${forwardToken.base64}&name=${name.base64}"
      }
      val ws = client.webSocketSession(
         host = config.host,
         port = config.port,
         path = path
      ).warp()

      ws.textFrameHandler {
         logger.info { "Received:${it.readText().debase64}" }
      }

      while (true) {
         val line = readLine()!!
         if (ws.isClosed()) break
         when (line) {
            "/exit" -> {
               ws.close()
            }
            else -> {
               ws.send("TestCli:$line".base64)
            }
         }
      }
   }

   override val coroutineContext: CoroutineContext
      get() = GlobalScope.coroutineContext
}
