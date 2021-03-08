package org.meowcat.mesagisto.kato

import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.Handlebars
import org.meowcat.mesagisto.kato.Plugin.CONFIG
import java.util.concurrent.ConcurrentHashMap

object Template {
  private val handlebars = Handlebars()
  private val cache = ConcurrentHashMap<String, HandlebarsTemplate>()
  private fun compile(name: String, input: String) {
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
    compile("join", CONFIG.template.join)
    compile("leave", CONFIG.template.leave)
    compile("death", CONFIG.template.death)
  }
  fun renderMessage(sender: String, content: String): String {
    val module = HashMap<String, String>(2)
    module.apply {
      put("sender", sender)
      put("content", content)
    }
    val context = Context.newContext(module)
    return apply("message", context)
  }

  fun renderJoin(player: String): String {
    val module = HashMap<String, String>(2)
    module.apply {
      put("player", player)
    }
    val context = Context.newContext(module)
    return apply("join", context)
  }
  fun renderLeave(player: String): String {
    val module = HashMap<String, String>(2)
    module.apply {
      put("player", player)
    }
    val context = Context.newContext(module)
    return apply("leave", context)
  }
  fun renderDeath(player: String, message: String): String {
    val module = HashMap<String, String>(2)
    module.apply {
      put("message", message)
      put("player", player)
    }
    val context = Context.newContext(module)
    return apply("death", context)
  }
}
