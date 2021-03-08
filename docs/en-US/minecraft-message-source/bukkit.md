# Bukkit message source

** The function of [Mesagisto](https://github.com/MeowCat-Studio/mesagisto) is to forward messages to the minecraft[bukkit] client **

## Requirement

- For windows, [Microsoft Visual C++ 2010 Redistributable runtime](https://www.microsoft.com/en-us/download/details.aspx?id=26999) needs to be installed. The number of bits of runtime should be consistent with JDK

## Installation

1. Download the jar archive on the [Releases Page](https://github.com/MeowCat-Studio/bukkit-message-source/releases)

2. Move the jar package to the plugins folder of the bukkit server (such as spigot, paper, etc.)

3. Start the server, and the configuration file will be automatically generated under the plugins folder

4. Modify `plugins/mesagisto/config.yml`
  ```yaml
  # Enable mesagisto
  enable: true
  # Your mesagisto channel, regardless of the value of channel,
  # As long as the channel values of different forwarding clients are the same
  channel: "your-channel"
  # Targetname of the server. Group chat/servers with the same target will not display messages from each other
  # This may be useful for those servers that have installed inter server message exchange
  target: "target-name"
  # Intermediate forwarding server, message bridge
  # The default is mesagisto commonweal [NATs](https://github.com/nats-io/nats-server) Server
  nats:
    address: nats://nats.mesagisto.org:4222
  # Encryption settings
  cipher:
    # The key used for encryption shall be the same at each end
    key: "your-key"
  ```

5. Save the configuration file and restart the bukkit server

## Matters needing attention

1. Avoid using hot overload. If there is a problem, please restart manually first
2. No matter what the value of channel is, as long as the channels bound by each forwarding client are the same
