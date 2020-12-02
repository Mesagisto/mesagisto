plugins {
    val kotlinVersion = "1.4.20"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("net.mamoe.mirai-console") version "1.1.0-dev-36"
}

mirai {
    publishing {
        repo = "mirai"
        packageName = "mirai-console-example-plugin"
        override = true
    }
}

group = "org.example"
version = "0.1.0"

repositories {
    mavenLocal()
    jcenter()
    mavenCentral()
}