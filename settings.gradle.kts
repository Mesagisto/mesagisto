rootProject.name = "message-forward"
include("message-dispatcher")

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




