
plugins {
   id ("com.github.johnrengelman.shadow") version ("5.2.0")
   java
   kotlin ("jvm") version "1.4.20"
}

group = "io.github.itsusinn.easyforward.bukkit"
version = "0.0.3-rc1"

val kotlinVersion = "1.4.20"
val vertxVersion = "3.8.0"
val junitJupiterVersion = "5.6.0"
val coroutineVersion = "1.4.1"
val jacksonVersion = "2.11.3"
val mccoroutine = "0.0.6"

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"

repositories {
   mavenCentral()
   jcenter()
   maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
   maven("https://oss.sonatype.org/content/groups/public/")

}

dependencies {

   compileOnly("org.spigotmc:spigot-api:1.16.3-R0.1-SNAPSHOT")
   compileOnly ("net.md-5:bungeecord-chat:1.16-R0.3")

   implementation("io.ktor:ktor-client-websockets:1.5.0")
   implementation ("com.squareup.okhttp3:okhttp:4.9.0")
   implementation("io.ktor:ktor-client-cio:1.5.0")

   //kotlin
   implementation(kotlin("stdlib-jdk8"))
   //jackson
   implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
   implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
   implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
   implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
   //coroutine
   compileOnly("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:$mccoroutine")
   implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:$mccoroutine")
   implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")
   implementation("io.github.microutils:kotlin-logging-jvm:2.0.2")
}
