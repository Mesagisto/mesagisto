package io.github.itsusinn.extension

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


private val loggerCache = HashMap<Any, Logger>()

/**
 * Needed dependencies
 * implementation("log4j:log4j:1.2.17")
 * implementation ("org.slf4j:slf4j-log4j12:1.7.30")
 * implementation ("org.slf4j:slf4j-api:1.7.30")
 */

val Any.logger:Logger
   get() =
      loggerCache.getOrPut(this){ LoggerFactory.getLogger(this.javaClass) }