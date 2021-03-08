import io.itsusinn.pkg.pkgIn
import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
  java
  id("dev.architectury.loom")
  id("architectury-plugin")
  id("org.jetbrains.kotlin.jvm")
  id("com.github.johnrengelman.shadow")
  id("io.itsusinn.pkg")
}
architectury {
  minecraft = "1.16.5"
  platformSetupLoomIde()
  fabric()
}
loom {
  silentMojangMappingsLicense()
  fabricApi { }
}
pkg {
  excludePath("mappings/*")
  excludePath("META-INF/*.kotlin_module")
  excludePath("META-INF/versions/*")
  excludePath("META-INF/proguard/*")
  excludePath("META-INF/maven/*")
  excludePath("META-INF/com.android.tools/*")
  excludePath("META-INF/services/kotlin.reflect*")
  excludePath("META-INF/services/java.security*")
  excludePath("org/slf4j/*")
  excludePath("org/jetbrains/annotations/*")
  excludePath("org/intellij/lang/annotations/*")
  excludePath("kotlin/*")
  excludePath("kotlinx/*")
  listOf("asn1", "jcajce", "jce", "pqc", "x509", "math", "i18n", "iana", "internal").forEach {
    excludePath("org/bouncycastle/$it/*")
  }
  val task = tasks.remapJar.get()
  task.dependsOn("pkg")
  shadowJar {
    task.inputFile.set(this.archiveFile)
  }
}
tasks {
  processResources {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") {
      expand(mutableMapOf("version" to project.version))
    }
  }
}

dependencies {
  modImplementation("net.fabricmc:fabric-loader:0.12.2")
  modImplementation("net.fabricmc.fabric-api:fabric-api:0.42.0+1.16")

  val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")
  minecraft("com.mojang:minecraft:1.16.5")
  mappings(loom.officialMojangMappings())

  pkgIn("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
  pkgIn(project(":common"))
}
