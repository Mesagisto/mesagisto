

plugins {
   id ("com.github.johnrengelman.shadow") version ("5.2.0")
   java
   kotlin ("jvm") version "1.4.20"
   id("org.jetbrains.kotlin.plugin.serialization") version("1.4.20")
}

group = "io.github.itsusinn.easyforward.bukkit"
version = "0.0.1"

val kotlinVersion = "1.4.20"
val vertxVersion = "3.8.0"
val coroutineVersion = "1.4.1"
val jacksonVersion = "2.11.3"

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"

repositories {
   mavenCentral()
   jcenter()
   maven("https://repo.codemc.org/repository/maven-public")
   maven("https://papermc.io/repo/repository/maven-public/")
   maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
   maven("https://oss.sonatype.org/content/groups/public/")

}

dependencies {

   implementation("net.mamoe:mirai-core-qqandroid:1.3.3")
   implementation("org.kodein.di:kodein-di:7.1.0")
   compileOnly("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:0.0.5")
   implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:0.0.5")
   implementation("com.charleskorn.kaml:kaml:0.24.0")

   compileOnly("org.spigotmc:spigot-api:1.16.3-R0.1-SNAPSHOT")
   implementation("com.squareup.okhttp3:okhttp:4.9.0")
   //vertx
   implementation("io.vertx:vertx-core:$vertxVersion"){
      exclude("io.netty")
   }
   compileOnly("io.netty:netty-all:4.1.6.Final")
   //kotlin
   implementation(kotlin("stdlib-jdk8"))
   //jackson
   implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
   implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
   implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
   implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

}
