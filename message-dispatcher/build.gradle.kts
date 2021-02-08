import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

group = Project.Group
version = Project.Version

application {
   mainClassName = "io.github.itsusinn.forward.dispatcher.Main"
}

val vertxVersion = "4.0.0"
val junitJupiterVersion = "5.6.0"
val coroutineVersion = "1.4.2"
val jacksonVersion = "2.11.3"

val compileKotlin: KotlinCompile by tasks

compileKotlin.kotlinOptions {
   freeCompilerArgs = listOf("-Xinline-classes")
   jvmTarget = "1.8"
}

dependencies {
   // vertx
   implementation("io.vertx:vertx-core:$vertxVersion")
   implementation("io.vertx:vertx-web:$vertxVersion")
   implementation("io.vertx:vertx-lang-kotlin-coroutines:$vertxVersion")
   implementation("io.vertx:vertx-lang-kotlin:$vertxVersion")
   implementation("io.vertx:vertx-ignite:$vertxVersion")
   // kotlin
   implementation(Dependency.Kotlin.StdLib)
   implementation(Dependency.KotlinX.Coroutine)
   implementation(Dependency.Kotlin.StbLib7)
   implementation(Dependency.Kotlin.StbLib8)

   // test
   testImplementation("io.vertx:vertx-junit5:$vertxVersion")
   testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")

   // jackson
   implementation(Dependency.Jackson.Core)
   implementation(Dependency.Jackson.DataBind)
   implementation(Dependency.Jackson.Annotations)
   implementation(Dependency.Jackson.KotlinModule)
   // logging
   implementation("org.slf4j:slf4j-api:1.7.30")
   implementation("io.github.microutils:kotlin-logging-jvm:2.0.2")
   implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.14.0")
   implementation("org.apache.logging.log4j:log4j-core:2.14.0")
   implementation("org.apache.logging.log4j:log4j-api:2.14.0")
}
