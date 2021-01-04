package io.github.itsusinn.forward.dispatcher.extension

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


private val loggerCache = HashMap<Any, Logger>()

val Any.logger:Logger
   get() =
      loggerCache.getOrPut(this){ LoggerFactory.getLogger(this.javaClass) }
