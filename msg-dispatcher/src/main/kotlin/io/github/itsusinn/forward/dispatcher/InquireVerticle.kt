package io.github.itsusinn.forward.dispatcher

import io.github.itsusinn.extension.endWithJson
import io.github.itsusinn.extension.jackson.asPrettyString
import io.github.itsusinn.extension.json
import io.vertx.kotlin.coroutines.CoroutineVerticle
/**
 * TODO inquire what channels does a app have
 */
class InquireVerticle:CoroutineVerticle() {
   override suspend fun start(){
      vertx
         .createHttpServer()
         .requestHandler {
            it.response().json().end(connMapper.status())
         }
         .listen(1432)
   }

}