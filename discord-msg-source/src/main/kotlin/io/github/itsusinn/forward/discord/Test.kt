package io.github.itsusinn.forward.discord

import io.github.itsusinn.extension.forward.data.Event
import io.github.itsusinn.extension.forward.data.FrameData
import io.github.itsusinn.extension.forward.data.TestEvent
import io.github.itsusinn.extension.forward.data.TestMessage
import io.github.itsusinn.extension.jackson.*
import io.github.itsusinn.extension.test.timing
import kotlin.reflect.jvm.jvmName

fun main(){

//   val test = TestEvent("this is test event")
   val test = TestMessage("this is test message")
   val frame = FrameData(
      code = 200,
      data = test
   )

   val text = frame.asString!!

   println(frame.asPrettyString)

}
fun parse(frame: String){
   val jsonFrame = readValue<FrameData>(frame)
      ?: throw NullPointerException()
   val data = jsonFrame.data
}