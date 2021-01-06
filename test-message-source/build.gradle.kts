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
application {
   mainClassName = "io.github.itsusinn.forward.test.App"
}
group = "io.github.itsusinn.forward"
version = "0.0.1"

val kotlinVersion = "1.4.21"
val vertxVersion = "3.9.4"
val junitJupiterVersion = "5.6.0"
val coroutineVersion = "1.4.1"
val jacksonVersion = "2.11.3"

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"

dependencies {
   //vertx
   implementation("io.vertx:vertx-core:$vertxVersion")
   implementation("io.vertx:vertx-web:$vertxVersion")
   implementation("io.vertx:vertx-lang-kotlin-coroutines:$vertxVersion")
   implementation("io.vertx:vertx-lang-kotlin:$vertxVersion")
   //kotlin
   implementation ("org.jetbrains.kotlin:kotlin-stdlib")
   implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")
   implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
   implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
   //test
   testImplementation("io.vertx:vertx-junit5:$vertxVersion")
   testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
   //jackson
   implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
   implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
   implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
   implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
   //logging
   implementation("org.slf4j:slf4j-api:1.7.30")
   implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.14.0")
   implementation("org.apache.logging.log4j:log4j-core:2.14.0")
   implementation("org.apache.logging.log4j:log4j-api:2.14.0")
}
