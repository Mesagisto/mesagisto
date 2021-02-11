package io.itsusinn.forward.mirai

import io.itsusinn.forward.client.KtorWebsocket
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Group
import java.util.concurrent.ConcurrentHashMap

// runtime data
// be clear after shutdown
val wsKeeper = ConcurrentHashMap<String, KtorWebsocket>()
val addressEntity = ConcurrentHashMap<String, HashSet<Group>>()

val groupHandler = ConcurrentHashMap<Long, Bot>()
val groupBots = ConcurrentHashMap<Long, HashSet<Bot>>()
