/**
 * Copyright 2019-2021 Itsusinn and contributors.
 *
 * Licensed under the GNU Lesser General Public License version 3.
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       https://opensource.org/licenses/LGPL-3.0
 */

pluginManagement {
   repositories {
      gradlePluginPortal()
      mavenCentral()
      jcenter()
      google()
      maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
      maven(url = "https://dl.bintray.com/kotlin/kotlin-dev")
      maven(url = "https://dl.bintray.com/jetbrains/kotlin-native-dependencies")
      maven(url = "https://kotlin.bintray.com/kotlinx")
   }
}

rootProject.name = "message-forwarding"

include("message-source:test-source")
findProject(":message-source:test-source")?.name = "test-source"

include("message-source:discord-source")
findProject(":message-source:discord-source")?.name = "discord-source"
