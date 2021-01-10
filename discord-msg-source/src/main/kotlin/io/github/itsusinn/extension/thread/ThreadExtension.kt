package io.github.itsusinn.extension.thread

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.*

open class SingleThreadLoop private constructor(
   private val executor: ExecutorService
):CoroutineScope {
   override val coroutineContext = executor.asCoroutineDispatcher()

   constructor() : this(Executors.newSingleThreadExecutor())
   constructor(parent:SingleThreadLoop) : this(parent.executor)

   fun shutdown() {
      executor.shutdown()
   }
   fun shutdownNow(){
      executor.shutdownNow()
   }
}