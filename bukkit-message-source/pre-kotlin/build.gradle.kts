plugins {
   java
   kotlin("jvm") version "1.4.20"
   id ("com.github.johnrengelman.shadow")version ("5.2.0")
}

group = "io.github.itsusinn.easyforward.bukkit"
version = "0.1.0-rc1"

repositories {
    mavenCentral()
}

dependencies {
   implementation(kotlin("stdlib"))

   implementation("org.spigotmc:spigot-api:1.16.3-R0.1-SNAPSHOT")
}
