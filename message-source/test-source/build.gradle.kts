/**
 * Copyright 2020-2021 Itsusinn and contributors.
 *
 * Licensed under the GNU Lesser General Public License version 3
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       https://opensource.org/licenses/LGPL-3.0
 */

plugins {
   java
   id("org.jetbrains.kotlin.jvm")
   id("com.github.johnrengelman.shadow")
}

group = Project.Group
version = Project.Version
tasks {


}
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
   implementation("com.github.meowcat-studio:message-forwarding-client:0.0.1")
   implementation("com.github.meowcat-studio:handy-dandy:0.0.5")
   implementation(Dependency.Ktor.Client.WebSocket)
   implementation(Dependency.Ktor.Client.CIO)
   implementation(Dependency.Okhttp)
}
