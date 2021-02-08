package io.itsusinn.extension.test

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
      println("""$block invoke $times times cost ${end - start}""")
   }
   return end - start
}
