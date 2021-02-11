
plugins {
   id("com.github.johnrengelman.shadow")
   java
   kotlin("jvm")
   id("org.jetbrains.kotlin.plugin.serialization")
}

group = Project.Group
version = Project.Version

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"

repositories {
   mavenCentral()
   jcenter()
}

dependencies {
}
