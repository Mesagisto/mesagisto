package io.github.itsusinn.forward.dispatcher

import io.vertx.kotlin.coroutines.CoroutineVerticle
/**
 * TODO inquire what channels does a app have
 */
class InquireVerticle:CoroutineVerticle() {
   override suspend fun start(){
      vertx.createHttpServer().listen(1431)
   }

}