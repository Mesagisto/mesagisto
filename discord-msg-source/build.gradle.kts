plugins {
   java
   kotlin("jvm") version "1.4.21"
}

group = "io.github.itsusinn.forward"
version = "0.0.1"

repositories {
   jcenter()
   mavenCentral()
}

dependencies {
   implementation(kotlin("stdlib"))
   implementation("net.dv8tion:JDA:4.2.0_225")
   testCompile("junit", "junit", "4.12")
}
