import io.itsusinn.pkg.pkgIn

group = "org.meowcat"
version = "1.4.1"
plugins {
  java
  kotlin("jvm") version "1.6.0"
  id("com.github.johnrengelman.shadow") version "7.1.1"
  id("io.itsusinn.pkg") version "1.2.2"
}
repositories {
  mavenCentral()
  mavenLocal()

  maven("https://oss.sonatype.org/content/repositories/snapshots/")
  maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}
pkg {
  excludePath("META-INF/*.kotlin_module")
  excludePathStartWith("META-INF/versions")
  excludePathStartWith("META-INF/proguard")
  excludePathStartWith("META-INF/maven")
  excludePathStartWith("org/jetbrains")
  excludePathStartWith("org/intellij")
  excludePath("*.md")
  excludePath("DebugProbesKt.bin")
  excludePathStartWith("kotlinx/coroutines/flow")
  listOf("asn1", "jcajce", "jce", "pqc", "x509", "math", "i18n", "iana", "internal").forEach {
    excludePathStartWith("org/bouncycastle/$it")
  }
  excludePathStartWith("META-INF/maven")

  shadowJar {
    minimize()
    mergeServiceFiles()
  }
  kotlinRelocate("org.yaml.snakeyaml", "$group.relocate.org.yaml.snakeyaml")
  relocateKotlinStdlib()
  relocateKotlinxLib()
}
java {
  targetCompatibility = JavaVersion.VERSION_1_8
  sourceCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
  pkgIn("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
  pkgIn("org.mesagisto:mesagisto-client:1.6.0-rc.7")
  pkgIn("com.github.jknack:handlebars:4.3.0")
  pkgIn("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.3")
  compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
}
tasks {
  processResources {
    inputs.property("version", project.version)
    filesMatching("plugin.yml") {
      expand(mutableMapOf("version" to project.version))
    }
  }
  compileKotlin {
    kotlinOptions {
      jvmTarget = "1.8"
      freeCompilerArgs = listOf("-Xinline-classes", "-opt-in=kotlin.RequiresOptIn")
    }
  }
}
