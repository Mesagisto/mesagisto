# Bungee message source

** The function of [Mesagisto](https://github.com/MeowCat-Studio/mesagisto) is to forward messages to the minecraft [bungee] client 客户端 **

## Requirement

- For windows, [Microsoft Visual C++ 2010 Redistributable runtime](https://www.microsoft.com/en-us/download/details.aspx?id=26999) needs to be installed. The number of bits of runtime should be consistent with JDK

## Installation

1. Download the jar archive on the [Releases Page](https://github.com/MeowCat-Studio/bungee-message-source/releases)

2. Move the jar package to the plugins folder of the bungeerecord server (such as waterfall velocity)

3. Start the server, and the configuration file will be generated automatically

4. Modify `plugins/mesagisto/config.yml` while ensuring** server shutdown **

```yaml
# Enable mesagisto
enable: true
# Your mesagisto channel binding
# It can be edited manually, but it is recommended to add through instructions
bindings:
  # Server name: mesagisto channel
  sub1: "test"
  sub2: "test"
# Encryption settings
cipher:
  # The key used for encryption {==(it is required to ensure that the forwarding ends are the same)==}
  key: "default"
# Intermediate forwarding server, message bridge
# The default is mesagisto commonweal [NATs](https://github.com/nats-io/nats-server) Server
nats: "nats://nats.mesagisto.org:4222"
# Message template
template:
  message: "§7<{{sender}}> {{content}}"
```
5. Save the configuration file and start the server.

6. Use `/msgist help` to view help in the sub server where mesagisto needs to be set, and use `/msgist [channel name]` to bind mesagisto channel


## Matters needing attention

1. The message source to be forwarded needs to be bound to the same** channel name **
2. Permission management: the permission node of this plug-in is `mesagisto`. If you want to use the mesagisto command, you need to grant this permission

=== "Do not use the rights management plug-in"

    Modify the bungee configuration file config.yml to
    ```yaml
    permissions:
      admin:
      - some-other-perm
      - {++mesagisto++}
    ......
    groups:
      playername:
      - default
      - {++admin++}
    ```
=== "LuckPerms"

    Execute the command in the server console that owns luckperms
    ```
    lpb user PlayerName permission set mesagisto
    ```
