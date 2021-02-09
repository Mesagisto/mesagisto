buildscript {
   repositories {
      mavenCentral()
      jcenter()
      google()
      maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
      maven(url = "https://kotlin.bintray.com/kotlinx")
      maven(url = "https://jitpack.io")
   }
   dependencies {
      classpath("com.android.tools.build:gradle:4.0.2")
      classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Kotlin}")
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
      maven(url = "https://jitpack.io")
   }
}
