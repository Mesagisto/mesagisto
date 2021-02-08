object Dependency {
   object Kotlin {
      private const val group = "org.jetbrains.kotlin"
      const val StdLib = "$group:kotlin-stdlib:${Versions.Kotlin}"
      const val StbLib7 = "$group:kotlin-stdlib-jdk7:${Versions.Kotlin}"
      const val StbLib8 = "$group:kotlin-stdlib-jdk8:${Versions.Kotlin}"
   }
   object KotlinX {
      private const val group = "org.jetbrains.kotlinx"
      const val Coroutine = "$group:kotlinx-coroutines-core:${Versions.Coroutine}"
   }
   object Ktor {
      private const val group = "io.ktor"
      const val Core = ""
      object Client {
         const val WebSocket = "$group:ktor-client-websockets:${Versions.Ktor}"
         const val CIO = "$group:ktor-client-cio:${Versions.Ktor}"
      }
   }
   object Jackson {
      private const val group = "com.fasterxml.jackson"
      const val Core = "$group.core:jackson-core:${Versions.Jackson}"
      const val DataBind = "$group.core:jackson-databind:${Versions.Jackson}"
      const val Annotations = "$group.core:jackson-annotations:${Versions.Jackson}"
      const val KotlinModule = "$group.module:jackson-module-kotlin:${Versions.Jackson}"
   }
   const val Okhttp = "com.squareup.okhttp3:okhttp:${Versions.Okhttp}"
   const val Slf4j = "org.slf4j:slf4j-api:${Versions.Slf4j}"
}
