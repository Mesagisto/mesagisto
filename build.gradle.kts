buildscript {
   repositories {
      mavenCentral()
      jcenter()
      google()
      maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
      maven(url = "https://kotlin.bintray.com/kotlinx")
   }
}
plugins {
   java
   application
   kotlin("plugin.serialization") version Versions.Kotlin
   id("org.jetbrains.kotlin.jvm") version Versions.Kotlin
   id("com.github.johnrengelman.shadow") version Versions.ShadowJar
   id("net.mamoe.mirai-console") version Versions.Mirai
}

allprojects {
   group = Project.Group
   version = Project.Version

   repositories {
      jcenter()
      maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
      maven(url = "https://kotlin.bintray.com/kotlinx")
      google()
      mavenCentral()
      maven(url = "https://dl.bintray.com/karlatemp/misc")
   }
}
