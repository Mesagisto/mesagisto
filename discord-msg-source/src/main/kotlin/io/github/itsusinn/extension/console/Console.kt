package io.github.itsusinn.extension.console

import io.github.itsusinn.extension.thread.SingleThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

typealias Handler = suspend Iterator<String>.() -> String?

object Console:CoroutineScope {
   private val thread = SingleThread.create("console-readline")
   val handlers = ConcurrentHashMap<String, Handler>()
   var helpInfo = "help"

   /**
    * Non Blocking
    */
   fun startListen() = async {
      while (true){
         readLine()?.split(" ")?.iterator()?.let {
            println(handleLine(it) )
         }
      }
   }
   private suspend fun handleLine(line: Iterator<String>):String{
      try {
         return doHandleLine(line) ?: helpInfo
      }catch (e:Exception){
         e.printStackTrace()
         return e.message ?: "Occur Exception ${e.javaClass.name}"
      }
   }
   private suspend fun doHandleLine(line: Iterator<String>) :String?{
      if (!line.hasNext()) return null
      val handler = handlers[line.next()]

      if (handler != null)
         return handler.invoke(line)
      else
         return null
   }

   fun handle(prefix:String, info:String = "No help info", handler:Handler){
      handlers[prefix] = handler
   }

   fun unregisterHandler(prefix: String){
      handlers.remove(prefix)
   }

   override val coroutineContext: CoroutineContext
      get() = thread.coroutineContext
}