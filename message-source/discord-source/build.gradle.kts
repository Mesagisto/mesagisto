plugins {
   java
   application
   id("org.jetbrains.kotlin.jvm")
   id("com.github.johnrengelman.shadow")
}
repositories {
   mavenCentral()
   jcenter()
}
application {
   mainClassName = "io.github.itsusinn.forward.discord.App"
}
group = "io.github.itsusinn.forward"
version = Versions.Project

val kotlinVersion = "1.4.20"
val coroutineVersion = "1.4.1"
val jdaVersion = "4.2.0_225"
val jdaUtilitiesVersion = "3.0.5"
val slf4jVersion = "1.7.30"
val log4jVersion = "2.14.0"

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"

dependencies {

   implementation("io.vertx:vertx-core:4.0.0")
   implementation("com.jagrosh:jda-utilities:$jdaUtilitiesVersion")
   implementation("net.dv8tion:JDA:$jdaVersion")
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
   implementation("org.slf4j:slf4j-api:$slf4jVersion")
   implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
   implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
   implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
   implementation("io.github.microutils:kotlin-logging-jvm:2.0.2")
}
