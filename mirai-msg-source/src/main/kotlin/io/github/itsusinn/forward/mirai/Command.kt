package io.github.itsusinn.forward.mirai

import io.github.itsusinn.extension.jackson.asPrettyString
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.MemberCommandSender
import net.mamoe.mirai.console.command.isUser
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.contact.isAdministrator
import net.mamoe.mirai.contact.isOperator

object ForwardCommand : CompositeCommand(
   ForwardPluginMain, "forward","f","fd",
   description = "Commands for Itsusinn's forward plugin"
){

   @SubCommand("setTarget","settarget","target")
   suspend fun MemberCommandSender.handleSetTarget() {
      if (user.isOperator()){
         ForwardConfig.target = subject.id
         sendMessage("set target to ${subject.id}")
      }
   }

   @SubCommand("status","状态")
   suspend fun CommandSender.handleStatus() {
      sendMessage(ForwardConfig.toString()?: "error")
   }
}