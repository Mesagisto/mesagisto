plugins {
   kotlin("jvm")
   java
   kotlin("plugin.serialization")
}
group = Project.Group
version = Project.Version

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"

dependencies {

   runtimeOnly(Dependency.Kotlin.StdLib)
   runtimeOnly(Dependency.KotlinX.Coroutine)

   implementation(project(Project.HandyDandy))

   // websocket
   implementation(project(Project.ForwardClient.Okhttp))
   implementation(Dependency.Ktor.Client.WebSocket)
   compileOnly(Dependency.Ktor.Client.CIO)
   compileOnly(Dependency.Okhttp)

   // jackson
   implementation(Dependency.Jackson.Core)
   implementation(Dependency.Jackson.DataBind)
   implementation(Dependency.Jackson.Annotations)
   implementation(Dependency.Jackson.KotlinModule)

   compileOnly("net.mamoe:mirai-core:2.3.2")
   compileOnly("net.mamoe:mirai-console:2.3.2")

   testImplementation("net.mamoe:mirai-core:2.3.2")
   testImplementation("net.mamoe:mirai-console:2.3.2")
   testImplementation("net.mamoe:mirai-console-terminal:2.3.2")

   implementation("io.github.microutils:kotlin-logging-jvm:2.0.2")
}
