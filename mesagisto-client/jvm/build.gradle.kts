plugins {
  id("org.jetbrains.kotlin.jvm") version "1.7.0"
  id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
  `maven-publish`
  signing
}
java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}
group = "org.mesagisto"
version = "1.7.0-SNAPSHOT"

nexusPublishing {
  repositories {
    sonatype { // only for users registered in Sonatype after 24 Feb 2021
      nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
      snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
    }
  }
}

publishing {
  publications {
    create<MavenPublication>("mavenJava") {
      from(components["java"])

      pom {
        name.set("mesagisto-client-jvm")
        // description.set("<<Component Description>>")-
        // url.set("<<Component URL>>")
        licenses {
          license {
            name.set("LGPL-2.1")
            // url.set("<<License URL>>")
          }
        }
        developers {
          developer {
            id.set("Itsusinn")
            name.set("iHsin")
            email.set("itsusinn@meowcat.org")
          }
        }
      }
    }
  }
}
signing {
  val signingKey: String? by project
  val signingPassword = ""
  useInMemoryPgpKeys(signingKey, signingPassword)
  sign(publishing.publications["mavenJava"])
}

repositories {
  mavenLocal()
  mavenCentral()
}
tasks.compileKotlin {
  kotlinOptions {
    jvmTarget = "11"
    freeCompilerArgs = listOf("-Xinline-classes", "-Xopt-in=kotlin.RequiresOptIn")
  }
}
tasks.test {
  useJUnitPlatform()
}
dependencies {

  implementation("org.ktorm:ktorm-core:3.5.0")
  implementation("org.ktorm:ktorm-support-sqlite:3.5.0")
  compileOnly("org.xerial:sqlite-jdbc:3.45.1.0")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4")

  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.16.1")
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-cbor:2.16.1")

  implementation("com.fasterxml.uuid:java-uuid-generator:4.3.0")
  implementation("io.nats:jnats:2.17.2")
  implementation("org.rfc8452.aead:AEAD:1.0.11")

  testImplementation(kotlin("test"))
}
