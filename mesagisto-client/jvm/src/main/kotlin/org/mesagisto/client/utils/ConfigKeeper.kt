@file:Suppress("ktlint:standard:no-wildcard-imports")

package org.mesagisto.client.utils

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import org.mesagisto.client.Logger
import java.nio.file.Path
import kotlin.io.path.*

val YAML =
  ObjectMapper(YAMLFactory()).apply {
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  }

data class ConfigKeeper<C>(
  val value: C,
  private val path: Path,
) {
  fun save() {
    path.writeText(YAML.writeValueAsString(value))
  }

  companion object {
    inline fun <reified T> create(
      path: Path,
      defaultValue: () -> T,
    ): ConfigKeeper<T> {
      val value =
        if (path.exists()) {
          Logger.info { "正在读取配置文件$path" }
          try {
            YAML.readValue(path.readText(), T::class.java)
          } catch (t: Throwable) {
            Logger.warn { "读取失败，可能是版本更新导致的." }
            Logger.error(t)
            path.moveTo(path.parent.resolve("${path.fileName}.old"), true)
            Logger.warn { "使用默认配置覆盖原配置，原配置已修改成$path.old" }
            val default = defaultValue()
            val defaultStr = YAML.writeValueAsString(default)
            path.writeText(defaultStr)
            default
          }
        } else {
          Logger.info { "配置文件不存在，新建默认配置 $path" }
          val default = defaultValue()
          val str = YAML.writeValueAsString(default)
          path.parent.createDirectories()
          path.createFile()
          path.writeText(str)
          default
        }
      val keeper = ConfigKeeper(value, path)
      keeper.save()
      return keeper
    }
  }
}
