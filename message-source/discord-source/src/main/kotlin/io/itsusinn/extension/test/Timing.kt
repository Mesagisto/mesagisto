package io.itsusinn.extension.test

import io.itsusinn.extension.jackson.jacksonLogger
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

fun timing(
   times: Int = 1,
   log: Boolean = true,
   block: () -> Unit,
): Long {
   val start = System.currentTimeMillis()
   for (i in 1..times) {
      block.invoke()
   }
   val end = System.currentTimeMillis()
   if (log) {
      jacksonLogger.debug("""$block invoke $times times cost ${end - start}""")
   }
   return end - start
}
