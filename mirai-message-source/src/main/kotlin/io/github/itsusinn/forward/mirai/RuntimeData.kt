package io.github.itsusinn.forward.mirai

import io.github.itsusinn.extension.forward.client.KtorWebsocket
import net.mamoe.mirai.contact.Group
import java.util.concurrent.ConcurrentHashMap


// be clear after shutdown
val wsKeeper = ConcurrentHashMap<String, KtorWebsocket>()

val addressEntity = ConcurrentHashMap<String,HashSet<Group>>()