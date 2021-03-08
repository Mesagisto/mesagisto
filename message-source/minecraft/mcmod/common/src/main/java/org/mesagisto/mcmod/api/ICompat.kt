package org.mesagisto.mcmod.api

import java.util.*

typealias Log4jLogger = org.apache.logging.log4j.Logger

interface ICompat {
  fun getLogger(): Log4jLogger
}
val CompatImpl by lazy {
  ServiceLoader.load(ICompat::class.java).findFirst().get()
}
