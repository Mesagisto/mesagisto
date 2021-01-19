package io.github.itsusinn.forward.bukkit

import io.github.itsusinn.extension.forward.client.KtorWebsocket
import java.util.concurrent.ConcurrentHashMap

val addressWsMapper = ConcurrentHashMap<String,KtorWebsocket>(1)