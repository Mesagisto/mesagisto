import io.itsusinn.pkg.pkgIn

group = "org.meowcat"
version = property("project_version")!!
plugins {
  java
  kotlin("jvm") version "1.5.21"
  kotlin("plugin.serialization") version "1.5.21"
  id("com.github.johnrengelman.shadow") version "7.1.1"
  id("io.itsusinn.pkg") version "1.2.0"
}
repositories {
  mavenCentral()
  mavenLocal()
  maven("https://jitpack.io")
  maven("https://repo.velocitypowered.com/snapshots/")
}
pkg {
  excludePath("META-INF/*.kotlin_module")
  excludePath("*.md")
  excludePath("DebugProbesKt.bin")
  excludePathStartWith("META-INF/maven")
  shadowJar {
    minimize()
    mergeServiceFiles()
  }
  relocateKotlinStdlib()
  relocateKotlinxLib()
}
java {
  targetCompatibility = JavaVersion.VERSION_1_8
}
tasks.compileKotlin {
  kotlinOptions {
    jvmTarget = "1.8"
    freeCompilerArgs = listOf("-Xinline-classes", "-Xopt-in=kotlin.RequiresOptIn")
  }
  sourceCompatibility = "1.8"
}
dependencies {
  compileOnly("org.jetbrains.kotlin:kotlin-stdlib")
  pkgIn("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-native-mt")
  pkgIn("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.3.2")
  pkgIn("io.nats:jnats:2.13.1")
  pkgIn("org.meowcat:mesagisto-client-jvm:1.1.2")

  compileOnly("com.velocitypowered:velocity-api:1.0.0-SNAPSHOT")
  annotationProcessor("com.velocitypowered:velocity-api:1.0.0-SNAPSHOT")
  // implementation("org.meowcat:mesagisto-client:1.1.1-n4")
}
