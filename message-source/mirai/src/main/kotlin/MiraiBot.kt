package org.meowcat.mesagisto.mirai

import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.BotOnlineEvent
import org.mesagisto.client.Logger

object MiraiBot {
  private var inner:Bot? = null
  fun botOnline(event: BotOnlineEvent){
    val bot = event.bot
    Logger.info { "Bot${bot.nick}-${bot.id} online." }
    inner = event.bot
  }
  fun getGroup(id:Long):Group? {
    return inner?.getGroup(id)
  }
}