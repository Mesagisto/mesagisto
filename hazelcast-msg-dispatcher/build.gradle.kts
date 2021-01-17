plugins {
    java
    kotlin("jvm") version "1.4.20"
}

group = "io.github.itsusinn"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
   implementation(kotlin("stdlib"))
   implementation ("io.vertx:vertx-hazelcast:4.0.0")
}
