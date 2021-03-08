plugins {
  java
  kotlin("jvm") version "1.7.0"
  id("com.github.johnrengelman.shadow") version "6.0.0"
  kotlin("plugin.serialization") version "1.7.0"
  id("net.mamoe.mirai-console") version "2.14.0"

  id("me.him188.maven-central-publish") version "1.0.0"
  id("io.codearte.nexus-staging") version "0.30.0"
  id("com.github.gmazzo.buildconfig") version "3.1.0"
}
group = "org.mesagisto"
version = "1.6.4"
buildConfig {
  val version = project.version.toString()
  buildConfigField("String", "VERSION", provider { "\"${version}\"" })
}
mavenCentralPublish {
  nexusStaging {
    serverUrl = "https://s01.oss.sonatype.org/service/local/"
    stagingProfileId = "9bdaa8e9e83392"
    username = credentials?.sonatypeUsername
    password = credentials?.sonatypePassword
  }
  useCentralS01()
  githubProject("Mesagisto", "mirai-message-source")
  licenseFromGitHubProject("AGPLv3", "master")
  developer("Itsusinn")
  publication {
    artifact(tasks.getByName("buildPlugin"))
  }
}

tasks.compileKotlin {
  kotlinOptions {
    jvmTarget = "1.8"
    freeCompilerArgs = listOf("-Xinline-classes", "-opt-in=kotlin.RequiresOptIn")
  }
}

repositories {
  mavenCentral()
  mavenLocal()
}

mirai {
  coreVersion = "2.14.0"
  jvmTarget = JavaVersion.VERSION_1_8
}

dependencies {
  implementation("org.ktorm:ktorm-core:3.5.0")
  implementation("org.ktorm:ktorm-support-sqlite:3.5.0")
  implementation("org.xerial:sqlite-jdbc:3.40.0.0")

  implementation("com.github.gotson:webp-imageio:0.2.2")
  implementation("org.mesagisto:mesagisto-client:1.6.1")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.4")
  compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.3.3")

  testImplementation("junit:junit:4.13.2")
  testImplementation("org.hamcrest:hamcrest:2.2")
}
