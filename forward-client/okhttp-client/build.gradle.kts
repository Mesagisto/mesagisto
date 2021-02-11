plugins {
   java
   kotlin("jvm")
}
group = Project.Group
version = Project.Version

repositories {
   mavenCentral()
}
val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"

dependencies {
   compileOnly(Dependency.Ktor.Client.WebSocket)
   compileOnly(Dependency.Ktor.Client.CIO)
   compileOnly(Dependency.Okhttp)
   compileOnly(Dependency.KotlinX.Coroutine)
   compileOnly("io.github.microutils:kotlin-logging-jvm:2.0.2")
}
