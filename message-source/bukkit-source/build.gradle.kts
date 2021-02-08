import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
plugins {
   id("com.github.johnrengelman.shadow")
   java
   kotlin("jvm")
}

val ProjectName = "bukkit-message-forward"

group = Project.Group
version = Project.Version

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

tasks.withType<ShadowJar> {
   exclude(
      "README.md",
      "module-info.class",
      "LICENSE",
      "Empty.class",
      "DebugProbesKt.bin",
      "CHANGELOG.md"
   )
   minimize {
      exclude(dependency(Dependency.Okhttp))
      exclude(dependency(Dependency.Ktor.Client.CIO))
      exclude(dependency(Dependency.Ktor.Client.WebSocket))
      exclude(dependency("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:$mccoroutine"))
      exclude(dependency("team.aura_dev.lib.slf4j-plugin.spigot:slf4j-plugin-spigot:1.2.0.39:1.7.25"))
   }
}

dependencies {

   api("org.spigotmc:spigot-api:1.16.3-R0.1-SNAPSHOT") {
      isTransitive = false
   }
   compileOnly("net.md-5:bungeecord-chat:1.16-R0.3")

   // websocket
   implementation(project(Project.ForwardClient.Okhttp))

   implementation(Dependency.Ktor.Client.WebSocket)
   implementation(Dependency.Ktor.Client.CIO)
   implementation(Dependency.Okhttp)

   // kotlin
   implementation(Dependency.Kotlin.StdLib)
   implementation(Dependency.KotlinX.Coroutine)

   // jackson
   implementation(Dependency.Jackson.Core)
   implementation(Dependency.Jackson.DataBind)
   implementation(Dependency.Jackson.Annotations)
   implementation(Dependency.Jackson.KotlinModule)

   // coroutine
   implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:$mccoroutine")
   implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:$mccoroutine")

   // logging
   implementation("io.github.microutils:kotlin-logging-jvm:2.0.2")
   implementation("org.slf4j:slf4j-api:1.7.30")
   implementation("team.aura_dev.lib.slf4j-plugin.spigot:slf4j-plugin-spigot:1.2.0.39:1.7.25") {
      isTransitive = false
   }
}
