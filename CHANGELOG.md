# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Unreleased

### Fixed

- The 'heal-indicators' setting now working.
- The possibility of the toggle database being accessed simultaneously by multiple threads.
- An error every time the database was accessed after an exception was thrown during an operation.


## 1.10.4 - 2024-02-24

### Fixed

- An error when showing indicators on servers older than 1.16.3.


## 1.10.3 - 2024-02-17

### Fixed

- A rare error when showing indicators.


## 1.10.2 - 2024-01-14

### Fixed
- Support for 1.20.4 using fallback mappings.
- Fallback mappings leaving ghost indicators if the server stops before they're removed.


## 1.10.1 - 2023-12-28

### Fixed

- The new options not applying to health indicators.


## 1.10.0 - 2023-12-23

### Added
- An option to hide indicators for invisible entities (disabled by default).
- An option to hide indicators for sneaking players (disabled by default).

### Changed
- Health indicators now show when being healed by less than half-a-heart.


## 1.9.0 - 2023-12-11

### Added
- Support for 1.20.4.


## 1.8.0 - 2023-11-10

### Added
- Support for 1.20.2.

### Fixed
- ArmorStands flickering on unsupported versions.


## 1.7.0 - 2023-08-19

### Added
- Support for 1.20.1.

### Fixed
- Support for 1.20 not working correctly.


## 1.6.0 - 2023-06-08

### Added
- Support for 1.20.


## 1.5.0 - 2023-03-15

### Added
- Support for 1.19.4.


## 1.4.1 - 2023-01-16

### Fixed
- An error when registering commands on legacy servers.


## 1.4.0 - 2022-12-09

### Added
- Support for 1.19.3.

### Fixed
- Toggles not working.

### Removed
- The `show-self-holograms` option.


## 1.3.4 - 2022-10-24

### Fixed
- 1.19.2 not working on Spigot (NMS mapping conflict with Paper).
- Some grammar in the default configs comments.


## 1.3.3 - 2022-10-21

### Fixed
- Indicators showing for zero damage events.

### Changed
- Re-licensed the project under the GNU GPLv3 license.


## 1.3.2 - 2022-08-10

### Fixed
- Indicators not disappearing when a player moves out of tracked range while it's visible.


## 1.3.1 - 2022-08-05

### Fixed
- Indicators sometimes not disappearing after killing an entity.


## 1.3.0 - 2022-08-05

### Added
- An option to let indicators move independently of the entity they came from (`relative-holograms`).
- An option to show your own indicators (`show-self-holograms`).

### Fixed
- An error when updating configs on legacy servers.

### Changed
- Optimized how what players to send packets to is determined.


## 1.2.1 - 2022-06-31

### Fixed
- `1.17` support not being enabled.


## 1.2.0 - 2022-06-09

### Added
- Support for 1.19.
- [bStats analytics](https://bstats.org/plugin/bukkit/Damage%20Indicator/15403).
- Options to disable damage and healing indicators separately.
- `/di plinfo` for debugging purposes.

### Fixed
- Config files not being regenerated under certain circumstances.

### Changed
- Moved the `messages.yml` contents to its own customizable locale file. Any changes will be migrated.


## 1.1.7 - 2022-03-04

### Added
- Support for 1.18.2.


## 1.1.6 - 2022-02-06

### Fixed
- Fixed indicators not showing from player-shot arrows when 'only-show-entity-damage-from-players' is enabled.


## 1.1.5 - 2021-12-18

### Fixed
- The first damage a player does not showing an indicator.
- Database requests not being run asynchronously.

### Changed
- Toggle database now uses UUIDs instead of names. All previous toggles will be lost.


## 1.1.4 - 2021-12-01

### Added
- Support for 1.18.

### Changed
- `entity-type-list-as-whitelist` is now `false` by default.


## 1.1.3 - 2021-09-16

### Added
- An option to only show entity damage indicators when the damage is dealt by a player.
- Entity type whitelist/blacklist.

### Fixed
- The bottom of the default help page being too short.
- Tab completion returning player names in slots with no args.
- An error when a player is damaged/ healed after disabling health-indicators with `/di reload`.
- An error when enabling health-indicators on 1.13+ servers.
- The plugin failing to load if another plugin or the server didn't initialize SQLite.
- Damage modified by other plugins sometimes not updating indicators.
- Packets being sent to all players no matter how far away they are.
- Indicators showing to the player who's been damaged/ healed.
- Health indicator not toggling if server was reloaded instead of using `/de reload`.

### Changed
- Optimized how indicators are handled.


## 1.1.2 - 2021-07-20

### Fixed
- A few bugs with health indicators.

### Changed
- Updated the default `messages.yml`.


## 1.1.1 - 2021-07-18

### Added
- Support for 1.17.

### Fixed
- An issue with health indicators conflicting with other plugins' scoreboards.


## 1.1.0 - 2021-07-11

### Added
- Support for 1.17.1.
- An option to disable indicators for specified entity types.
- An option to toggle health indicators being displayed below players' names.

### Removed
- Support for 1.17.


## 1.0.0 - 2021-07-03

- Initial release.

### Added
- Support for 1.17.


## 0.1.0 - 2021-05-26

### Added
- The `disabled-worlds` option in config.
- Commands.
- Configurable messages.

### Fixed
- Fixed an issue with indicators showing `-0` on low heals.
- Fixed an issue with incorrect indicators showing on `/kill`.

### Changed
- Display for 0-damage items now use the format under `damage-format` instead of `heal-format`.
- Slight changes to default config.
- Rewrote internal file utilities.


## 0.0.3 - 2021-05-16

### Added
- Options to bold, italicise and underline indicators.

### Fixed
- Indicators showing in no pvp zones.
- Indicators randomly showing in other worlds.


## 0.0.2 - 2021-04-02

### Fixed
- An issue with 1.8 compatibility.
- Indicators showing even if the damage/heal event was cancelled by another plugin.
- Indicators showing for entities like armor stands and boats.


## 0.0.1 - 2021-04-02

- Initial pre-release.
