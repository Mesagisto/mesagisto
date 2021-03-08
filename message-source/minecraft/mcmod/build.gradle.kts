plugins {
  java
  id("dev.architectury.loom") version "1.3-SNAPSHOT" apply false
  id("architectury-plugin") version "3.4-SNAPSHOT"

  id("org.jetbrains.kotlin.jvm") version ("1.6.0")
  id("com.github.johnrengelman.shadow")version ("7.1.0")
  id("io.itsusinn.pkg") version "1.2.2"
}
allprojects {
  group = "org.mesagisto"
  version = "1.3.1"
  repositories {
    mavenLocal()
    mavenCentral()
  }
}
