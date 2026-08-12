# Chat Heads Vintage

Chat Heads Vintage is a client-side Minecraft 1.12.2 backport of
[Chat Heads](https://github.com/dzwdz/chat_heads). It displays a player's skin head next to chat messages so senders
are easier to distinguish.

## Features

- Shows each detected sender's skin head and hat layer beside their name, or in a neat column at the left of chat
- Heads fade with chat and can have an optional shadow and 3D hat effect
- Keeps wrapped messages, hover text and clickable chat links aligned, including when timestamp or chat-format mods move
  sender names
- Offers accurate `STRICT` sender detection or broader `HEURISTIC` detection for server display names and nicknames
- Lets you add nickname-to-username aliases manually or learn them automatically from EssentialsX `/realname` replies
- Can optionally show heads in system messages and command suggestions from Cleanroom, Brigo or Salutation
- Provides settings through Forge's Mods screen and updates chat when they are changed

Manual aliases use `nickname=MinecraftUsername`, for example `ServerNickname=RealName`.

## Command suggestion compatibility

Chat Heads Vintage can show player heads in these command suggestion lists:

- [Cleanroom](https://download.cleanroommc.com/) 0.6.10-alpha or newer
- [Brigo](https://modrinth.com/mod/brigo)
- [Salutation](https://github.com/Speiger/Salutation)

When using Cleanroom 0.6.10 or later, please do not install Forge command autocomplete mods such as Brigo or Salutation alongside it.

A head is shown only when a suggestion exactly matches the Minecraft username of a player who is currently online,
including capitalization. Nicknames, aliases and incomplete command text do not receive heads. The suggestion list
automatically makes room for each head and keeps the entries visible and clickable.

## Compatibility

- Revo Font
- Smooth Font
- Cleanroom 0.6.10-alpha or newer
- Salutation
- Brigo

## Incompatible

- Revo UI

## Credits and license

The original Chat Heads mod was created by [dzwdz](https://github.com/dzwdz). This backport is maintained by
[Sumire Labs](https://github.com/Sumire-Labs).

Chat Heads Vintage is licensed under the [Mozilla Public License 2.0](LICENSE.md).
