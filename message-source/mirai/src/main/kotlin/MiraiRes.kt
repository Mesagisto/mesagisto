package org.meowcat.mesagisto.mirai

import org.mesagisto.client.Res
import java.nio.file.Path
import kotlin.io.path.createDirectories

object MiraiRes {
  private val Dir = Res.Directory.resolve("qq").apply { createDirectories() }
  suspend fun convert(
    id: String,
    variant: String,
    converter: suspend (Path, Path) -> Result<Unit>
  ): Result<Path> {
    val before = Res.get(id) ?: return Result.failure(IllegalStateException("Non exists"))
    val after = Dir.resolve("$variant-$id}")
    converter.invoke(before, after)
    return Result.success(after)
  }
}
