package io.itsusinn.extension.runtime

val runtime = Runtime.getRuntime()

fun addShutdownHook(hook: Runnable) {
   runtime.addShutdownHook(Thread(hook))
}

fun exit(status: Int) {
   runtime.exit(status)
}
