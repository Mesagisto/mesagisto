plugins {
   java
   application
   id("org.jetbrains.kotlin.jvm")
   id("com.github.johnrengelman.shadow")
}
application {
   mainClassName = "io.github.itsusinn.forward.test.App"
}
group = Project.Group
version = Project.Version

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"

dependencies {

   // kotlin
   implementation(Dependency.Kotlin.StdLib)
   implementation(Dependency.Kotlin.StbLib7)
   implementation(Dependency.Kotlin.StbLib8)
   implementation(Dependency.KotlinX.Coroutine)
   // jackson
   implementation(Dependency.Jackson.Core)
   implementation(Dependency.Jackson.DataBind)
   implementation(Dependency.Jackson.Annotations)
   implementation(Dependency.Jackson.KotlinModule)
   // logging
   implementation("org.slf4j:slf4j-api:1.7.30")
   implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.14.0")
   implementation("org.apache.logging.log4j:log4j-core:2.14.0")
   implementation("org.apache.logging.log4j:log4j-api:2.14.0")
   implementation("io.github.microutils:kotlin-logging-jvm:2.0.2")

   // websocket
   implementation(project(Project.ForwardClient.Okhttp))

   implementation(Dependency.Ktor.Client.WebSocket)
   implementation(Dependency.Ktor.Client.CIO)
   implementation(Dependency.Okhttp)
}
