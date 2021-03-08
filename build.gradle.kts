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
   id("org.jetbrains.kotlin.jvm") version Versions.Kotlin
   id("com.github.johnrengelman.shadow") version Versions.ShadowJar
}

allprojects {
   group = Project.Group
   version = Project.Version

   repositories {
      mavenCentral()
      jcenter()
      maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
      maven(url = "https://kotlin.bintray.com/kotlinx")
      google()
      maven(url = "https://jitpack.io")
   }
}
