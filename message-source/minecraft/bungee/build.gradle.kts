import io.itsusinn.pkg.pkgIn

plugins {
  java
  kotlin("jvm") version "1.6.0"
  kotlin("plugin.serialization") version "1.6.0"
  id("com.github.johnrengelman.shadow") version "7.1.1"
  id("io.itsusinn.pkg") version "1.2.2"
}

group = "org.meowcat"
version = "1.0.0-rc"

repositories {
  mavenCentral()
  mavenLocal()

  maven("https://oss.sonatype.org/content/repositories/snapshots/")
  maven("https://oss.sonatype.org/content/groups/public/")
  maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}
pkg {
  excludePath("META-INF/*.kotlin_module")
  excludePathStartWith("META-INF/versions")
  excludePathStartWith("META-INF/proguard")
  excludePathStartWith("META-INF/maven")
  excludePathStartWith("org/slf4j")
  excludePathStartWith("org/jetbrains")
  excludePathStartWith("org/intellij")
  excludePath("*.md")
  excludePath("DebugProbesKt.bin")
  excludePathStartWith("kotlinx/coroutines/flow")
  listOf("asn1", "jcajce", "jce", "pqc", "x509", "math", "i18n", "iana", "internal").forEach {
    excludePathStartWith("org/bouncycastle/$it")
  }

  shadowJar {
    minimize()
    mergeServiceFiles()
  }
  kotlinRelocate("org.yaml.snakeyaml", "relocate.org.yaml.snakeyaml")
  relocateKotlinStdlib()
  relocateKotlinxLib()
}
dependencies {
  compileOnly("net.md-5:bungeecord-api:1.18-R0.1-SNAPSHOT")
  compileOnly("org.jetbrains.kotlin:kotlin-stdlib")

  pkgIn("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
  pkgIn("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.3")
  pkgIn("com.github.jknack:handlebars:4.3.0")
  pkgIn("org.mesagisto:mesagisto-client:1.6.0-rc.1")
}
java {
  targetCompatibility = JavaVersion.VERSION_1_8
  sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks {
  processResources {
    inputs.property("version", project.version)
    filesMatching("bungee.yml") {
      expand(mutableMapOf("version" to project.version))
    }
  }
  compileKotlin {
    kotlinOptions {
      jvmTarget = "1.8"
      freeCompilerArgs = listOf("-Xinline-classes", "-Xopt-in=kotlin.RequiresOptIn")
    }
    sourceCompatibility = "1.8"
  }
}
