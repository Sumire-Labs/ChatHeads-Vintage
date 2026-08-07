# Changelog

## [0.1.0] - 2026-08-07

### Added

- Initial Minecraft 1.12.2 Forge/Cleanroom client implementation.
- Player-head rendering before vanilla chat lines with skin and hat layers.
- Configurable rendering immediately before the detected sender name (`BEFORE_NAME`, default) or before the full line
  (`BEFORE_LINE`).
- Multi-line wrapping, text offset, opacity, and click/hover position handling.
- Name-position relocation after timestamp/chat-format prefixes and formatting-code-aware inline rendering.
- Structured and heuristic sender detection with manual nickname aliases.
- Automatic, persistent nickname alias learning from EssentialsX `/realname` responses.
- Forge standard configuration with live chat reflow.
- Runtime exclusion for NeoFontRender UI Enhancements.
- Optional command-suggestion head adapters for Brigo 1.1.1 and Salutation 1.0.2, including background, screen-edge and
  mouse hit-box layout adjustments.
- Forge and Cleanroom loader handling without requiring an external MixinBooter jar on Cleanroom.
- Runtime compatibility validation for Smooth Font 2.1.4 and NeoFontRender core 0.5.2.
- Runtime class-transformation validation against the real reobfuscated Brigo and Salutation jars.
- Unit tests for name matching, command parsing, structured sender evidence, wrapped-line metadata, suggestion lookup,
  layout and optional-adapter reflection boundaries.
