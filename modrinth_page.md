![](https://imgur.com/9lCOZaz.png)

<p align="center">

## A high performance packet-based damage indicator

</p>

DamageIndicator is an extensive and feature-rich healthbar manager for Minecraft servers.
It provides a system to **display damage dealt** to enemies solely using **packets**,
which means no actual armor stand entities will be spawned.

DamageIndicator is also **completely server-side** so **no additional mods are required** by your players.
The **high-performance** packet spawning and **flexible configuration** makes DamageIndicator extremely suitable for large PvP or Survival servers.

![](https://imgur.com/5pw7FBU.png)

- **Highly configurable**
    - Modify damage indicator offset, speed and duration
    - Edit all indicator colours to your liking
        - Each damage range can be a different colour group
        - The colour of every character in a colour group can be customised
    - Support for bold, italics and rainbow mode
    - Global toggle for heal indicators in addition to damage indicators

- **No setup required**
    - Default offset and colour values have been extensively tested on large servers
    - Comes with 10 unique colour groups for all indicators to provide more variation
    - Simply drag & drop the plugin to get started

- **Cross-version compatibility**
    - Supports _all_ Minecraft versions from 1.8–1.21.7
    - Incompatible versions will fallback to invisible armor stands instead of breaking the plugin

- **Per-player & per-world toggle support**
    - Allow players to disable client-side indicator packets via a command
        - Useful to preserve FPS on low-end setups and for experienced PvP players who do not need indicators
    - Admins can completely disable indicators in specific worlds
        - Useful for non-PvP worlds or in fast-paced gamemodes like NoHitDelay

- **High-performance**
    - DamageIndicator was built with performance in mind
    - No performance issues on servers with up to 100+ players
    - Perfect for large PvP or Survival servers

- **100% packet-based**
    - DamageIndicator uses purely packets to send indicators to players
    - Hugely boosts performance as no armor stand entities are actually spawned
    - No floating armor stands will remain in the world if your server crashes

![](https://imgur.com/T8HeNXQ.png)

<p align="center">

![](https://imgur.com/cvJQEmi.gif) ![](https://imgur.com/b20ktC3.gif)
![](https://imgur.com/CxEtL5i.gif) ![](https://imgur.com/YfnsIrO.gif)

</p>

![](https://imgur.com/hqxxKx8.png)

> **/damageindicator help**  
> [damageindicator.command.help]  
> _Help command to show all possible arguments_

> **/damageindicator toggle <on/off>**  
> [damageindicator.command.toggle]  
> _Toggle client-side damage indicators_

> **/damageindicator reload**  
> [damageindicator.command.reload]  
> _Reload the plugin’s `config.yml` and `messages.yml` files_

![](https://imgur.com/uceYuev.png)

```yaml
# Config wiki and help can be found at https://github.com/Insprill/Damage-Indicator/wiki
# For internal reference only, do not change the config version
config-version: 4

# The locale file the plugin is using. To create your own locale, copy the en-us.yml, rename it, and set its name here.
language: "en-us"

# How many decimal places to truncate indicator values
# A value of 2 means that all damage indicators will show as x.xx
indicator-decimals: 2

# How many blocks should indicators spawn above the player
# It is best to keep this value nonzero to prevent indicators
# from blocking the player's view if show-self-holograms is enabled.
hologram-offset: 0.25
# How fast should indicators float upwards
hologram-speed: 0.15
# How long (in ticks) should indicators stay for
hologram-duration: 20
# Whether the position of holograms should stay relative to the entity they came from.
relative-holograms: true
# Whether holograms should be randomly offset on the X and Z axis from the player up to 0.5 blocks in any direction.
random-hologram-offset: false

# Whether all damage indicators should be bold
bold-indicators: true

# Whether all damage indicators should be italicized
italic-indicators: false

# Whether all damage indicators should be underlined
underline-indicators: false

# Whether damage indicators should be shown.
damage-indicators: true

# Format used when players take damage
# Each damage value represents half a heart (20 points = 10 hearts)
# The closest rounded-down value will be used
damage-format:
  0: "%fefee%-%value%" # <-- The %fefee% placeholder means the indicator will show as &f-&e0&f.&e6&f9 if the player takes 0.69 damage
  2: "%f6fee%-%value%"
  4: "%fcfcc%-%value%"
  7: "%f4fcc%-%value%"
  9: "%f4f44%-%value%"
  10: "%f44fdd%-%value%"
  12: "%f55fdd%-%value%"
  100: "%rainbow%-%value%" # <-- The %rainbow% placeholder can be used to randomize indicator colors (fancy display for insta-kill tools like /kill)

# Format used when players deal a critical hit.
# This works exactly the same as the `damage-format` option above.
# Uncomment to enable.
# crit-damage-format:
#   0: "%rainbow%-%value%"

# Whether damage heal indicators should be shown.
heal-indicators: true

# Format used when players heal
# Each heal value represents half a heart (20 points = 10 hearts)
# The closest rounded-down value will be used
heal-format:
  0: "%fafaa%+%value%"
  4: "%f2faa%+%value%" # <-- The %fafaa% placeholder means the indicator will show as &f+&a0&f.&a6&a9 if the player heals by 4.20 points
  8: "%f2f22%+%value%"

# Feature to display player health below their name
health-indicators: true

# DamageIndicator will not display any (damage) indicators in the following worlds
# World names are cAsE sEnSiTiVe
disabled-worlds:
  - nodelay
  - testworld

# If enabled, damage indicators will only be shown if a player caused it.
only-show-entity-damage-from-players: true

# if enabled, indicators will not be shown for players/entities that are invisible.
ignore-invisible-entities: false

# if enabled, indicators will not be shown for players that are sneaking.
ignore-sneaking-players: false

# Toggles the below entity type whitelist/blacklist.
entity-type-list-enabled: true
# If true, the following list will act as a whitelist instead of a blacklist.
entity-type-list-as-whitelist: false
entity-type-list:
  - ARMOR_STAND

# DamageIndicator will not display any (damage) indicators for entities whose name is in this list.
ignored-entities: [ ]
```

![](https://imgur.com/1x5d7La.png)

<p align="center">

![](https://imgur.com/er5gpPL.png)
![](https://imgur.com/zXv4Smv.png)
![](https://imgur.com/bttUgkK.png)

</p>

- Developer up to 1.1.2 | Zenya4
- Configs up to 1.1.2 & Beta Testing | Skaian
- Videos & Editing | Greylatte
