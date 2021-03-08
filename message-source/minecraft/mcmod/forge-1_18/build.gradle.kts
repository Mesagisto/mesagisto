import io.itsusinn.pkg.pkgIn
import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
  java
  kotlin("jvm")
  id("architectury-plugin")
  id("dev.architectury.loom")
  id("com.github.johnrengelman.shadow")
  id("io.itsusinn.pkg")
}
architectury {
  minecraft = "1.18.2"
  platformSetupLoomIde()
  forge()
}
repositories {
  maven("https://files.minecraftforge.net/maven")
  maven("https://maven.minecraftforge.net")
  maven("https://maven.parchmentmc.org")
  mavenCentral()
}
pkg {
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
  kotlinRelocate("org.yaml.snakeyaml", "relocate.org.yaml.snakeyaml")
}

loom {
  silentMojangMappingsLicense()
}

dependencies {
  val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")
  minecraft("com.mojang:minecraft:1.18.2")
  mappings(loom.officialMojangMappings())

  forge("net.minecraftforge:forge:1.18.2-40.1.0")
  compileOnly("org.jetbrains.kotlin:kotlin-stdlib")

  pkgIn(project(":common"))
}

java {
  targetCompatibility = JavaVersion.VERSION_1_8
  sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks {
  compileKotlin {
    kotlinOptions {
      jvmTarget = "1.8"
      freeCompilerArgs = listOf("-Xinline-classes", "-Xopt-in=kotlin.RequiresOptIn")
    }
    sourceCompatibility = "1.8"
  }
  processResources {
    inputs.property("version", project.version)
    filesMatching("META-INF/mods.toml") {
      expand("version" to project.version)
    }
  }
}
