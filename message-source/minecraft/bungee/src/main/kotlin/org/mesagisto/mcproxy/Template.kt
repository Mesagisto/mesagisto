package org.mesagisto.mcproxy

import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.Handlebars
import org.mesagisto.mcproxy.Plugin.CONFIG
import java.util.concurrent.ConcurrentHashMap

object Template {
  private val handlebars = Handlebars()
  private val cache = ConcurrentHashMap<String, HandlebarsTemplate>()
  fun compile(name: String, input: String) {
    if (cache.contains(name)) {
      cache.remove(name)
    }
    cache.getOrPut(name) {
      handlebars.compileInline(input)
    }
  }
  fun apply(name: String, context: Context): String {
    val handlebars = cache[name]!!
    return handlebars.apply(context)
  }
  fun remove(name: String) {
    cache.remove(name)
  }
  fun init() {
    compile("message", CONFIG.template.message)
  }
}
