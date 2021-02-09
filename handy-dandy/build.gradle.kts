plugins {
   java
   kotlin("jvm")
}
group = Project.Group
version = Project.Version

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"

dependencies {
   compileOnly("io.github.microutils:kotlin-logging-jvm:2.0.2")
   // jackson
   compileOnly(Dependency.Jackson.Core)
   compileOnly(Dependency.Jackson.DataBind)
   compileOnly(Dependency.Jackson.Annotations)
   compileOnly(Dependency.Jackson.KotlinModule)

   compileOnly(Dependency.KotlinX.Coroutine)

   compileOnly(Dependency.Okhttp)
}
