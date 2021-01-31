rootProject.name = "easy-forward"
include("message-dispatcher")
include("mirai-message-source")
include("forward-client:okhttp-client")
findProject(":forward-client:okhttp-client")?.name = "okhttp-client"
include("minecraft-message-source:bukkit-message-source")
findProject(":minecraft-message-source:bukkit-message-source")?.name = "bukkit-message-source"
