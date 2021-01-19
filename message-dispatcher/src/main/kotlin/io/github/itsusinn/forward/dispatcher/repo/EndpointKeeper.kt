package io.github.itsusinn.forward.dispatcher.repo

import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.MessageConsumer
import io.vertx.core.http.ServerWebSocket
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

object EndpointKeeper {

   //address-consumer receive from other verticles' publish
   val sharedConsumer = ConcurrentHashMap<String,MessageConsumer<String>>()
   val clientCounter = ConcurrentHashMap<String,AtomicInteger>()
}