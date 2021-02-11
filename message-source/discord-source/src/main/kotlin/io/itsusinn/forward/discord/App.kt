// package io.github.itsusinn.forward.discord
//
// import com.jagrosh.jdautilities.command.CommandClientBuilder
// import com.jagrosh.jdautilities.command.CommandEvent
// import io.github.itsusinn.extension.config.ConfigKeeper
// import io.github.itsusinn.extension.forward.WebForwardClient
// import io.github.itsusinn.extension.forward.data.FrameData
// import io.github.itsusinn.extension.forward.data.TextMessage
// import io.github.itsusinn.extension.jackson.asBuffer
// import io.github.itsusinn.extension.jackson.asBytes
// import io.github.itsusinn.extension.jda.DiscordBotClient
// import io.github.itsusinn.extension.jda.listenEvent
// import io.github.itsusinn.extension.runtime.exit
// import io.github.itsusinn.extension.thread.SingleThreadCoroutineScope
// import io.github.itsusinn.extension.vertx.eventloop.eventBus
// import io.github.itsusinn.forward.discord.command.addBindCommand
// import io.github.itsusinn.forward.discord.command.addPingCommand
// import io.vertx.core.buffer.Buffer
// import kotlinx.coroutines.launch
// import mu.KotlinLogging
// import net.dv8tion.jda.api.Permission
// import net.dv8tion.jda.api.entities.ChannelType
// import net.dv8tion.jda.api.entities.Message
// import net.dv8tion.jda.api.entities.MessageChannel
// import net.dv8tion.jda.api.events.message.MessageReceivedEvent
// import java.io.File
//
// object App : SingleThreadCoroutineScope("forward") {
//   //Make sure the forward folder exists
//   init { File("forward").apply { mkdir() } }
//
//   val configKeeper = ConfigKeeper
//      .create<ConfigData>(
//         defaultConfig, File("forward/discord.json")
//      )
//
//   val config = configKeeper.config
//   private val logger = KotlinLogging.logger(javaClass.name)
//
//   /**
//    * about start signal,
//    * see [ConfigData]
//    */
//   @JvmStatic fun main(args:Array<String>){
//
//      if (config.startSignal >1) {
//         config.startSignal--
//         logger.warn { "Config dont exist,write default config into forward/discord.json" }
//         logger.warn { "app will exit,please modify config" }
//         configKeeper.save()
//      } else if (config.startSignal == 1){
//         try {
//            launch {
//               start()
//            }
//         }catch (e:Throwable){
//            logger.error(e) { "start up failed \n" + e.stackTrace  }
//            exit(1)
//         }
//      } else {
//         logger.warn { "app has been prohibited to start" }
//      }
//   }
//
//   suspend fun start() {
//
//      val discordClient = DiscordBotClient.create(
//         token = config.discord.token)
//
//      val forwardClient = WebForwardClient.create(
//         port = config.forward.port,
//         host = config.forward.host,
//         uri = config.forward.uri,
//         appID = config.forward.appID,
//         channelID = config.forward.channelID,
//         token = config.forward.token,
//         name = config.forward.name)
//
//      val commands = CommandClientBuilder()
//         .setPrefix("/")
//         .setOwnerId("795231031082876939")
//         .setHelpWord("help")
//         .addPingCommand()
//         .addBindCommand()
//         .build()
//
//      discordClient.addEventListener(commands)
//
//      listenEvent<MessageReceivedEvent>("forward-message"){
//         when(channelType){
//            ChannelType.TEXT -> {
//               logger.debug { "dc received ${message.contentDisplay}" }
//               if (channel.idLong != 795234861081952256) return@listenEvent
//               if (this.author.isBot) return@listenEvent
//               val textMessage = TextMessage(member!!.idLong,message.contentDisplay)
//               val forwardFrame = FrameData(200,textMessage)
//               val messageBuffer = forwardFrame.asBuffer
//                  ?: return@listenEvent
//               logger.debug { "de write data" }
//               forwardClient.writeFinalBinaryFrame(messageBuffer)
//            }
//            ChannelType.PRIVATE -> {
//               logger.info {
//                  "Received Private Message ${message.contentDisplay}"
//               }
//            }
//            else -> { }
//         }
//      }
//      val myChannel = discordClient.textChannels.find { it.idLong == 795234861081952256 }
//      forwardClient.frameHandler {
//         logger.debug { "Received:${it.textData()}" }
//         myChannel?.sendMessage(it.textData())?.queue()
//      }
//   }
// }
