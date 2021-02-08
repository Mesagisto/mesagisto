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

rootProject.name = "message-forward"
include("message-dispatcher")
include("handy-dandy")

include("forward-client:okhttp-client")
findProject(":forward-client:okhttp-client")?.name = "okhttp-client"

include("forward-client:vertx-client")
findProject(":forward-client:vertx-client")?.name = "vertx-client"

include("message-source:bukkit-source")
findProject(":message-source:bukkit-source")?.name = "bukkit-source"

include("message-source:mirai-source")
findProject(":message-source:mirai-source")?.name = "mirai-source"

include("message-source:test-source")
findProject(":message-source:test-source")?.name = "test-source"

include("message-source:discord-source")
findProject(":message-source:discord-source")?.name = "discord-source"

// When building android target,please remove the comment on the next line.
// When using intellij,please add it.
// But when you commit,please remove it.
include(":message-source:mirai-android-source")
findProject(":message-source:mirai-android-source")?.name = "mirai-android-source"
