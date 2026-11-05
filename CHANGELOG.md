# Changelog for Enemy Echelons API 1.21.1 on NeoForge

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.0] - 2026-11-03

Brings the NeoForge API up to the Forge 1.20.1 API 1.1.0.

### Added
- Echelon configs, difficulty-name configs and exclusions can be loaded from datapacks (including a mod's own jar), alongside the existing TOML configs. They are re-read on `/reload`.
  - `data/<namespace>/echelons/<name>.json` - one echelon config. The id is the file's resource location, ex. `mymod:deep`.
  - `data/<namespace>/difficulty_names/<name>.json` - one difficulty-name config.
  - `data/<namespace>/echelons/exclusions/<name>.json` - `{ "mods": [...], "mobs": [...] }`. Every echelon config in the global registry (TOML and datapack) treats these as blacklisted, and an exclusion wins over every whitelist. Excluded mobs are left untouched (difficulty unset), so a mod's own custom registry can still apply to them.
- Keys are the snake_case forms of the TOML keys, plus an optional `load_order` (default 99). See the Forge API's README ("Datapack Configs") for the full key list and examples; the format is the same on both loaders.
- Datapack files are strict: an unknown key or a malformed value is logged as an error naming the file, and that file is skipped.
- With datapack configs present, the registry is rebuilt from the TOML configs plus the datapack configs, sorted by (load_order, id). TOML configs are never removed by a reload.
- Mobs that have already spawned keep their difficulty; a reload only affects new spawns.
- `replace` now works: a config with `replace = true` takes the place (and precedence) of every earlier config with the same id, TOML or datapack. Without it, configs with the same id are all kept, as before.
- `/eechelons dump` (permission level 2) lists the global echelon configs in order of precedence with their source (toml or data), the configs left out (ex. replaced), the exclusions and the difficulty names, in chat and in the log.
- `EnemyEchelonsApi.hasDifficultyCapability(Entity)`, as on Forge: whether the entity can carry a difficulty (any `Mob`).

### Fixed
- `EnemyEchelonsApi.apply(...)` never did anything on a freshly spawned mob. It checked `hasDifficulty()`, which on NeoForge is only true once the difficulty attachment has been written. It now checks that the entity is a `Mob`, as the Forge capability did.
- The flying speed modifier was never applied, and was added under the movement speed modifier's id. It is now applied, under its own id, as on Forge.
- Finding the config for a mob that isn't in any mob/mod whitelist no longer stops at the first config whose `modWhitelist` doesn't include the mob's mod.
- A config with only a `mobWhitelist` now applies to those mobs only.
- A mob wildcard, ex. `"minecraft:*"`, in a difficulty-name config's mob lists crashed while loading the config. It now moves to the matching mod list. An invalid mob id is skipped with a warning.
- `DifficultyNameRegistryEntry.registerDifficultyName()` threw a NullPointerException on an entry that had no names yet.
- Rebuilding the echelon or difficulty-name registry no longer leaves it briefly empty for lookups on other threads.

### Changed
- Requires GottschCore 2.7.0 or newer (`[2.7.0,3.0.0)`). GottschCore's version format changed from `1.21.1-2.x.x` to `2.x.x`, which the old range rejected. GottschCore now loads before this mod (`ordering="AFTER"`).
- The version scheme follows GottschCore's: the version is now `1.1.0` (was `1.21.1-1.0.0`), and the Maven artifact is `eechelonsapi-neoforge-1.21.1` (was `eechelonsapi-neoforge`). NeoForge sorts `1.1.0` below `1.21.1-1.0.0`, so a dependency range must use the new form, ex. `[1.1.0,2.0.0)`.
- None of the existing public API changed.

## [1.0.0] - 2025-08-02

Initial Release.
Uses Open Software License v. 3.0 (OSL-3.0) insetad of LGNU 3.0
