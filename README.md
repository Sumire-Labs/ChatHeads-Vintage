# Chat Heads Vintage

Chat Heads Vintage is a client-side Minecraft 1.12.2 backport of
[Chat Heads](https://github.com/dzwdz/chat_heads). It displays a player's skin head next to chat messages so senders
are easier to distinguish.

The port is currently in early development. Its `BEFORE_NAME` and `BEFORE_LINE` vanilla-chat paths have been
runtime-smoke-tested on Forge, and its core rendering path has also been tested on Cleanroom. Broad modpack and custom
chat-format coverage still needs testing.

## Requirements

- Minecraft 1.12.2
- Forge 14.23.5.2847 with [MixinBooter 11+](https://github.com/CleanroomMC/MixinBooter), or
- Cleanroom Loader, using its built-in MixinBooter implementation (0.6.8-alpha tested)

Cloth Config and ConfigAnyTime are not required. Settings use Forge's standard configuration system and are available
from the Mods screen.

## Implemented

- Heads immediately before detected sender names (`BEFORE_NAME`, the default) or in a fixed column before chat lines
  (`BEFORE_LINE`), including the skin's hat layer
- Chat opacity/fade applied to heads
- Optional head shadow and hat-layer 3D effect
- Consistent wrapping for both render positions, including names moved by timestamp and chat-format mods
- Correct text hover/click hit testing around inline and line-prefix heads
- `STRICT` detection using structured reply commands attached to player names
- `HEURISTIC` detection using online profile names, tab-list display names and manual aliases
- Optional automatic alias learning from EssentialsX `/realname` responses, persisted through Forge's standard config
- Optional detection in system messages
- Forge configuration hot reload and chat reflow
- Optional player heads in command-suggestion lists supplied by Brigo or Salutation

Name aliases use `nickname=profileName`, for example:

```text
S:nameAliases <
    ServerNickname=RealMinecraftName
>
```

With automatic alias detection enabled, a standard EssentialsX response such as `ServerNickname is RealMinecraftName`
adds or updates the same mapping automatically.

Minecraft 1.12.2 chat packets do not contain the sender UUID. Servers that remove both the profile name and structured
reply command from a message may therefore require an alias, and some custom chat formats cannot be identified
reliably.

## Command suggestion compatibility

Chat Heads Vintage includes optional adapters with no hard dependency on either suggestion mod:

- [Brigo](https://modrinth.com/mod/brigo) Forge 1.1.1
- [Salutation](https://github.com/Speiger/Salutation) 1.0.2

An adapter is loaded only when its target mod is present. A suggestion receives a head only when its entire text is an
exact, case-sensitive online profile name; display names, configured chat aliases and command fragments are deliberately
excluded. When at least one matching player is present, the adapter reserves a 12-pixel head column, extends the
background and mouse hit box, and moves the list right if the new column would leave the screen. Suggestion text is
still measured and drawn by the active `FontRenderer`.

Both adapters have been tested for successful Mixin application against their real reobfuscated Forge jars on Java 8.
Interactive multiplayer coverage of drawing, scrolling and clicking the augmented lists is still needed. The currently
unreleased Cleanroom command-suggestion implementation is intentionally deferred until its release shape and target
classes stabilize.

## Font compatibility

The main rendering path modifies `GuiNewChat` call sites and delegates all width measurement, wrapping and glyph
drawing to the active `FontRenderer`. It does not mix into `FontRenderer` itself.

- Vanilla `FontRenderer`: runtime smoke-tested on Forge
- Smooth Font 2.1.4: `BEFORE_NAME` rendering and hit testing runtime-smoke-tested on the obfuscated Forge/Java 8 client
  path
- NeoFontRender 0.5.2 core: runtime smoke-tested on Cleanroom 0.6.8-alpha with ModularUI 3.1.6; vanilla chat using its
  custom font renderer is supported. The earlier core smoke test used `BEFORE_LINE`; broader `BEFORE_NAME` pack
  coverage remains useful.
- NeoFontRender UI Enhancements 0.3.2: unsupported and automatically excluded because it replaces the same chat
  pipeline and includes its own chat-head renderer; the exclusion path is runtime-tested

Smooth Font and NeoFontRender installed together are not a supported combination.

## Development

```powershell
.\gradlew.bat setupDecompWorkspace
.\gradlew.bat test
.\gradlew.bat build
.\gradlew.bat runClient
```

The development environment uses RetroFuturaGradle and targets Java 8 bytecode.

Smooth Font's old ASM transformer produces a verifier error in the deobfuscated development client unless that client
is launched with `-noverify`. The normal obfuscated Java 8 client path was tested without `-noverify` and completed the
Chat Heads rendering and hit-testing smoke test.

## Credits and license

The original Chat Heads mod was created by [dzwdz](https://github.com/dzwdz). This backport is maintained by
[Sumire Labs](https://github.com/Sumire-Labs).

Chat Heads Vintage is licensed under the [Mozilla Public License 2.0](LICENSE.md).
