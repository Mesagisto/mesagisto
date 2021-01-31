plugins {
    val kotlinVersion = "1.4.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.0.0" // mirai-console version
}

mirai {
    coreVersion = "2.0.0" // mirai-core version
}

group = "io.github.itsusinn"
version = "0.1.0"

val jacksonVersion = "2.11.3"

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
}
dependencies {
   implementation ("io.ktor:ktor-client-websockets:1.5.0")
   implementation("io.github.microutils:kotlin-logging-jvm:2.0.2")
   //jackson
   implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
   implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
   implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
   implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
   implementation ("net.mamoe:mirai-slf4j-bridge:1.1.0")
}