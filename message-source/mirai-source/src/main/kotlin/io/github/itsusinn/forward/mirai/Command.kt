package io.github.itsusinn.forward.mirai

import io.github.itsusinn.forward.mirai.Config.addressTokenRepo
import io.github.itsusinn.forward.mirai.Config.targetAddressMapper
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.contact.isOperator

object ForwardCommand : CompositeCommand(
   ForwardPluginMain, "forward","f","fd",
   description = "Commands for Itsusinn's forward plugin."
){

   @SubCommand("setTarget","target","st")
   suspend fun MemberCommandSender.handleSetTarget() {
      if (!user.isOperator()) return

      sendMessage("set target to ${subject.id}")
   }

   @SubCommand("listChannel","lc")
   suspend fun MemberCommandSender.handleListChannel(){
      if (!user.isOperator()) return

      if (Config.addressTokenRepo.size == 0) {
         sendMessage("""
            There is no address stored in storage.
            Please add some in private chat.
         """.trimIndent())
         return
      }
      sendMessage("There are(is) ${addressTokenRepo.size} address(es) stored in storage.")
      val storage = StringBuilder()
      val iterator = addressTokenRepo.keys.iterator()
      while (iterator.hasNext()){
         storage.append(iterator.next())
         if (iterator.hasNext()) storage.append("\n")
      }
      sendMessage(storage.toString())
      sendMessage("""Please input "/forward setChannel [address]" to set channel.""")
   }
   @SubCommand("setChannel","sc")
   suspend fun MemberCommandSender.handleSetChannel(address: String){
      if (!user.isOperator()) return
      if (!addressTokenRepo.containsKey(address)){
         sendMessage("The address you put doesn't exist in storage!")
         return
      }
      addressEntity.remove(address)
      targetAddressMapper.put(group.id,address)
      sendMessage("Set channel successfully")
   }

   @SubCommand("setHost","st")
   suspend fun FriendCommandSender.handleSetHost(host: String){
      Config.host = host
      sendMessage("Set host successfully")
   }
   @SubCommand("setPort","sp")
   suspend fun FriendCommandSender.handleSetPort(port: Int){
      Config.port = port
      sendMessage("Set port successfully")
   }

   @SubCommand("addChannel","ac")
   suspend fun FriendCommandSender.handleAddChannel(address:String,token:String){
      if (
         !address.contains(".") ||
         address.startsWith(".") ||
         address.endsWith(".")
      ){
         sendMessage("the pattern of channel's address is [appID.channelID].")
         return
      }
      sendMessage("Successfully add address into storage.")
      addressTokenRepo.put(address,token)
   }

}