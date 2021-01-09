package io.github.itsusinn.extension.runtime

fun addShutdownHook(hook:Runnable){
   Runtime.getRuntime().addShutdownHook(Thread(hook))
}