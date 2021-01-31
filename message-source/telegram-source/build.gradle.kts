plugins {
    val kotlinVersion = "1.4.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
}



group = "org.example"
version = "0.1.0"

repositories {
    mavenLocal()
    jcenter()
    mavenCentral()
}
