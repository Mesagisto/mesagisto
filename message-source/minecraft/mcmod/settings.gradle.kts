rootProject.name = "mcmod-message-source"
pluginManagement {
  repositories {
    maven("https://maven.fabricmc.net/")
    maven("https://maven.architectury.dev/")
    maven("https://maven.minecraftforge.net/")
    maven("https://files.minecraftforge.net/maven/")
    gradlePluginPortal()
    mavenLocal()
    maven("https://github.com/Itsusinn/maven-repo/raw/master/")
  }
}
include("common")
include("fabric-1_16", "fabric-1_17")
include("fabric-1_18", "fabric-1_19")
include("fabric-1_20")
include("forge-1_18")
