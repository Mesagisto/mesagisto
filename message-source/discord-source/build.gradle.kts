plugins {
   java
   application
   id ("org.jetbrains.kotlin.jvm") version ("1.4.20")
   id ("com.github.johnrengelman.shadow") version ("5.2.0")
}
repositories {
   mavenCentral()
   jcenter()
}
application{
   mainClassName = "io.github.itsusinn.forward.discord.App"
}
group = "io.github.itsusinn.forward"
version = "0.0.1"

val kotlinVersion = "1.4.20"
val coroutineVersion = "1.4.1"
val jacksonVersion = "2.11.3"
val jdaVersion = "4.2.0_225"
val jdaUtilitiesVersion = "3.0.5"
val slf4jVersion = "1.7.30"
val log4jVersion = "2.14.0"

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"
compileKotlin.targetCompatibility = "1.8"

dependencies {

   implementation("io.vertx:vertx-core:4.0.0")
   implementation("com.jagrosh:jda-utilities:$jdaUtilitiesVersion")
   implementation("net.dv8tion:JDA:$jdaVersion")
   //kotlin
   implementation("org.jetbrains.kotlin:kotlin-stdlib")
   implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")
   implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
   implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")

   //jackson
   implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
   implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
   implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
   implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
   //logging
   implementation("org.slf4j:slf4j-api:$slf4jVersion")
   implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
   implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
   implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
   implementation ("io.github.microutils:kotlin-logging-jvm:2.0.2")


}
