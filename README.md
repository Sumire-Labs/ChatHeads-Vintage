# Chad Heads CRE

Chad Heads CRE is a client-side Minecraft 1.12.2 backport of
[Chat Heads](https://github.com/dzwdz/chat_heads), built exclusively for
[Cleanroom](https://download.cleanroommc.com/) 0.7.14-alpha or newer and Java 25.
It displays a player's skin head next to chat messages so senders are easier to distinguish.

## Features

- Shows each detected sender's skin head and hat layer beside their name, or in a column at the left of chat
- Heads fade with chat and can have an optional shadow and 3D hat effect
- Keeps wrapped messages, hover text and clickable chat links aligned
- Offers `STRICT` sender detection or broader `HEURISTIC` detection for server display names and nicknames
- Supports manual nickname aliases and learns aliases from EssentialsX `/realname` replies
- Shows heads in system messages and Cleanroom's built-in command suggestions
- Provides settings through the Mods screen and updates chat when they are changed

Manual aliases use `nickname=MinecraftUsername`, for example `ServerNickname=RealName`.

## Command suggestions

Cleanroom's built-in suggestion list is the supported command completion interface.
A head is shown only when a suggestion exactly matches the username of an online player,
including capitalization. Nicknames, aliases and incomplete command text do not receive heads.
The list makes room for heads and keeps entries visible and clickable.

## Compatibility

- Better Chat 1.4 for Minecraft 1.12.2
- Revo Font
- Smooth Font

Revo UI is incompatible because it replaces the same chat pipeline and supplies its own heads.

## Building and releases

Run `./gradlew build` with JDK 25. The build follows the official
[CleanroomModTemplate](https://github.com/CleanroomMC/CleanroomModTemplate)
(commit `1863fbb4267f2318ee5b3c8e137605c8b8c92c37`), using CleanroomGradle 0.22.0,
Conventions 1.4.0 and Gradle 9.8.2. Run `./gradlew runClient` to start the development client.
The mod targets Java 25 and requires Cleanroom; its mod ID and configuration file name are
`chad_heads_cre`. Existing settings can be copied from `config/chat_heads.cfg` to
`config/chad_heads_cre.cfg` before starting the game.

Versions are derived from git tags. Use `./gradlew -q printVersion` to inspect the version,
and push a version tag such as `0.2.0` to run the release workflow.
The workflow builds the jars, creates a GitHub Release and publishes to the configured Modrinth
and CurseForge projects via `publishMods`. Configure `modrinth_project_id` and
`curseforge_project_id` in `gradle.properties`, and set the matching repository secrets
`MODRINTH_TOKEN` and `CURSEFORGE_TOKEN`.

## Credits and license

The original Chat Heads mod was created by [dzwdz](https://github.com/dzwdz).
This backport is maintained by [Sumire Labs](https://github.com/Sumire-Labs).
Chad Heads CRE retains the [Mozilla Public License 2.0](LICENSE.md).
