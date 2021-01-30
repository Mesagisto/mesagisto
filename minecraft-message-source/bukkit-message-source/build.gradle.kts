import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
plugins {
   id ("com.github.johnrengelman.shadow") version ("5.2.0")
   java
   kotlin ("jvm") version "1.4.20"
}

val ProjectVersion = "0.1.0"
val ProjectName = "bukkit-message-forward"

group = "io.github.itsusinn.easyforward.bukkit"
version = ProjectVersion

val kotlinVersion = "1.4.20"
val vertxVersion = "3.8.0"
val junitJupiterVersion = "5.6.0"
val coroutineVersion = "1.4.1"
val jacksonVersion = "2.11.3"
val mccoroutine = "0.0.6"

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"



repositories {
   mavenCentral()
   jcenter()
   maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
   maven("https://oss.sonatype.org/content/groups/public/")
   maven("https://maven.aura-dev.team/repository/auradev-releases/")

}

tasks.withType<ShadowJar>{
   exclude(
      "README.md",
      "module-info.class",
      "LICENSE",
      "Empty.class",
      "DebugProbesKt.bin",
      "CHANGELOG.md"
   )
   archiveBaseName.set(ProjectName)
   archiveVersion.set(ProjectVersion)
   minimize {
      exclude(dependency("com.squareup.okhttp3:okhttp:4.9.0"))
      exclude(dependency("io.ktor:ktor-client-cio:1.5.0"))
      exclude(dependency("io.ktor:ktor-client-websockets:1.5.0"))
      exclude(dependency("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:$mccoroutine"))
      exclude(dependency("team.aura_dev.lib.slf4j-plugin.spigot:slf4j-plugin-spigot:1.2.0.39:1.7.25"))
   }
}


dependencies {

   compileOnly("org.spigotmc:spigot-api:1.16.3-R0.1-SNAPSHOT"){
      isTransitive = false
   }
   compileOnly("net.md-5:bungeecord-chat:1.16-R0.3")

   //websocket
   implementation("io.ktor:ktor-client-websockets:1.5.0")
   implementation ("com.squareup.okhttp3:okhttp:4.9.0")
   implementation("io.ktor:ktor-client-cio:1.5.0")

   //kotlin
   implementation(kotlin("stdlib"))

   //jackson
   implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
   implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
   implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
   implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

   //coroutine
   implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:$mccoroutine")
   implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:$mccoroutine")
   implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")

   //logging
   implementation("io.github.microutils:kotlin-logging-jvm:2.0.2")
   implementation("org.slf4j:slf4j-api:1.7.30")
   implementation("team.aura_dev.lib.slf4j-plugin.spigot:slf4j-plugin-spigot:1.2.0.39:1.7.25"){
      isTransitive = false
   }


}

