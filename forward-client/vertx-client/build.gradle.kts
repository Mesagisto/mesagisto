
plugins {
   id ("com.github.johnrengelman.shadow") version ("5.2.0")
   java
   kotlin ("jvm") version "1.4.20"
   id("org.jetbrains.kotlin.plugin.serialization") version("1.4.20")
}

group = "io.github.itsusinn.easyforward.bukkit"
version = "0.0.2-rc5"

val kotlinVersion = "1.4.20"
val vertxVersion = "3.8.0"
val coroutineVersion = "1.4.1"
val jacksonVersion = "2.11.3"

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"

repositories {
   mavenCentral()
   jcenter()
}

dependencies {


}
