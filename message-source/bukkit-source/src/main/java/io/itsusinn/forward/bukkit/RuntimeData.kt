package io.itsusinn.forward.bukkit

import io.itsusinn.forward.client.KtorWebsocket
import java.util.concurrent.ConcurrentHashMap

val addressWsMapper = ConcurrentHashMap<String, KtorWebsocket>(1)
