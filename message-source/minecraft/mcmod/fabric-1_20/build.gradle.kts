import io.itsusinn.pkg.pkgIn
import net.fabricmc.loom.api.LoomGradleExtensionAPI

repositories {
  mavenCentral()
  mavenLocal()
  maven("https://maven.fabricmc.net/")
}

plugins {
  java
  id("dev.architectury.loom")
  id("architectury-plugin")

  id("org.jetbrains.kotlin.jvm")
  id("com.github.johnrengelman.shadow")
  id("io.itsusinn.pkg")
}
architectury {
  minecraft = "1.20.4"
  platformSetupLoomIde()
  fabric()
}
loom {
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
  modImplementation("net.fabricmc:fabric-loader:0.15.3")
  modImplementation("net.fabricmc.fabric-api:fabric-api:0.93.1+1.20.4")

  minecraft("com.mojang:minecraft:1.20.4")
  mappings("net.fabricmc:yarn:1.20.4+build.3:v2")

  pkgIn(project(":common"))
  pkgIn("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
}
