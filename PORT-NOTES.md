# Port notes: Enemy Echelons API 1.21.1-1.1.0 (NeoForge)

Branch `1.21.1-api-feature/1.1.0`, off `1.21.1-api-main` (1.21.1-1.0.0).
Source: the Forge API `gottsch-minecraft-EnemyEchelons`, branch `1.20.1-api-main` at 1.1.0
(`290eb74`, "add replace semantics and /eechelons dump", 2026-10-29).

**Nothing on this branch has been compiled or run.** It was ported by reading and editing source
only. Work through "Compile and test locally" below before merging.

## Version numbers

- `mod_version` is `1.21.1-1.1.0`, matching the Forge API's 1.1.0.
- Your local checkout (`neoforge-EnemyEchelonsAPI-1.21.1`) has an uncommitted `1.21.1-1.0.1` bump
  (CHANGELOG, `build.gradle`, `gradle.properties`, `neoforge.mods.toml`, `update.json`). It isn't on GitHub.
  This branch skips 1.0.1. Its one change (GottschCore `[2.7.0,3.0.0)` and the
  `gottsch:gottschcore-neoforge-1.21.1` coordinate) is folded into 1.1.0, so you can discard those local
  edits. If you keep them, those five files will conflict with this branch when you merge.
- `update.json` lists only 1.0.0 and 1.1.0.

## What was ported (Forge 1.0.0 → 1.1.0)

The NeoForge API was ported from Forge 1.0.0. Forge 1.0.1 only fixed `update.json`. Everything below came
in with 1.1.0 and its two follow-up fix commits.

| Area | Files |
|---|---|
| Datapack configs: codecs, loader, reload listener | `core/data/EchelonDataCodecs`, `EchelonDataLoader`, `EchelonDataReloadListener` (new) |
| Exclusions | `core/config/EchelonExclusions` (new), `EchelonRegistry.isExcluded`, `EchelonManager` checks |
| `load_order` | `EchelonConfigsHolder.applyLoadOrder()`/`loadOrder`, `NameConfigsHolder.loadOrder`, `Config.java` calls `applyLoadOrder()` |
| `replace` semantics; TOML and data sources kept apart; atomic index swap; whitelist search fix | `core/registry/EchelonRegistry`, `DifficultyNameRegistry` |
| Wildcard and invalid mob ids in name configs, NPE in `registerDifficultyName` | `core/registry/DifficultyNameRegistryEntry` |
| `/eechelons dump` | `core/command/EchelonsCommand`, `RegistryDump` (new); registered in `WorldEventHandler` |
| Reload listeners | `WorldEventHandler.onAddReloadListeners` (`AddReloadListenerEvent`, GAME bus) |
| Flying speed modifier id | `EchelonManager.modifyFlyingSpeed` uses `ECHELON_FLYING_SPEED_MODIFIER` |
| Unit tests (2 classes) and test data | `src/test/java/.../core/data/*Test.java`, `src/test/resources/{data,toml}` |
| Build | JUnit 5 + `neoForge.unitTest` in `build.gradle`; `publishAll` task; `gottschcore_version=2.7.0` in `gradle.properties` |

The registry, config-holder, codec, loader, command and test files are the Forge 1.1.0 files with only
mechanical changes: package `mod.gottsch.neoforge.eechelons`, `mod.gottsch.neo.gottschcore`, `MOD_ID` → `MODID`,
and `new ResourceLocation(..)` → `ResourceLocation.fromNamespaceAndPath(..)` / `ResourceLocation.parse(..)`.
I diffed the existing NeoForge files against Forge 1.0.0 first: those files had no NeoForge-specific changes
beyond the same mechanical ones, so nothing was lost by taking the Forge 1.1.0 versions.

New classes carry `@author Mark Gottschling on 11/03/2026`.

### NeoForge-only fixes (not in the Forge diff, but needed for parity)

1. **`apply(...)` was a no-op on fresh mobs.** The 1.0.0 port replaced the Forge capability check with
   `entity.hasData(DIFFICULTY)`, which is false until the attachment is first written. So
   `applyModifications` returned at once for every newly spawned mob, and Dungeons2's integration could
   never scale or pin anything. The guards now use the new `EchelonManager.hasDifficultyCapability(entity)`
   (`entity instanceof Mob`, like the Forge capability attach). `getData()` then creates the attachment
   at -1. `hasDifficulty()` keeps its meaning: a difficulty has been written.
2. **`EnemyEchelonsApi.hasDifficultyCapability(Entity)` added**, the name Forge Dungeons2's
   `EchelonsIntegration` calls. When you port Dungeons2's integration, use it where Forge used
   `hasDifficultyCapability`. `getDifficulty` returns `Integer` here, not `int`, but it never returns null.
3. **Flying speed was never applied.** `applyModifications(Config, Mob, Integer)` didn't call
   `modifyFlyingSpeed`. It does now, as on Forge.
4. **`neoforge.mods.toml`:** GottschCore range `[2.7.0,3.0.0)`, and `ordering` changed from `BEFORE` to
   `AFTER`. A library this mod depends on must load first.

### Not ported

- Forge's `capability/*`, `network/*` and `setup/CommonSetup` have no counterpart here. The 1.0.0 port
  replaced them with data attachments (`ModDataAttachements`), and 1.1.0 didn't touch them.
- `EnemyEchelonsApi.getDifficultyCapability()` (it returned Forge's `IDifficultyHandler`). Use
  `getDifficulty`/`getDifficultyName`.
- The Forge README's "Datapack Configs" section. This repo's README is still the NeoForge MDK template.
  The CHANGELOG points to the Forge README, where the format is the same.
- The main mod (`1.21.1-main`, 2.0.1). Out of scope: only the API needed parity.

## PORT-CHECK list

| Where | What to confirm |
|---|---|
| `core/data/EchelonDataCodecs.java` (`isResourceLocation`) | `ResourceLocation.isValidResourceLocation(String)` is gone in 1.21.1, so it was replaced with `ResourceLocation.tryParse(v) != null`. Also confirm `ResourceLocation.isValidNamespace(String)` is still public static. |
| `core/data/EchelonDataReloadListener.java` | `SimpleJsonResourceReloadListener(Gson, String)` and `apply(Map<ResourceLocation, JsonElement>, ResourceManager, ProfilerFiller)` (the 1.21.1 shape; 1.21.2 changed it). The `echelons` listener must still see `echelons/exclusions/*` (the loader filters them out by prefix). |
| `core/event/WorldEventHandler.java` | `net.neoforged.neoforge.event.AddReloadListenerEvent#addListener`, and that it fires on the GAME bus on server start and `/reload`. |

Things that look right but are worth a look:
- `build.gradle` uses `neoForge { unitTest { enable(); testedMod = mods."${mod_id}" } }`, as Dungeons2 does.
  The tests only use `ResourceLocation`, Gson and NightConfig, so plain JUnit might be enough. Keep this if it works.
- `ResourceLocation.fromNamespaceAndPath(".", ".")` (`ALL_MOBS`, `ALL_DIMENSION`) was already in 1.0.0.

## Compile and test locally

1. Make sure GottschCore ≥ 2.7.0 is published under `gottsch:gottschcore-neoforge-1.21.1` (mavenLocal or `../maven`).
2. `./gradlew compileJava compileTestJava`. Settle the three PORT-CHECKs.
3. `./gradlew test`. Both ported test classes (`EchelonDataCodecsTest`, `EchelonRegistryDataTest`) should pass
   unchanged. Message texts come from `EchelonDataCodecs`, so an assertion on an error message for an
   invalid id should still match.
4. `./gradlew runClient` (or a server) with no datapacks. The log should show
   `loaded 0 echelon config(s) from data` and similar. `/eechelons dump` should list the TOML configs with
   source `toml`.
5. Add a test datapack with one file of each kind (copy `src/test/resources/data/eechelonstest`), run
   `/reload` and `/eechelons dump`, and check that the exclusions and the `load_order` ordering show up.
6. Spawn a zombie in a configured range. Check that it gets a difficulty and modifiers (fix 1). Also check a
   flying mob, ex. a bee or parrot, gets its flying speed modifier with no "Modifier is already applied" error.
7. `./gradlew publishAll`, then wire up Dungeons2's `EchelonsIntegration` against `1.21.1-1.1.0` and restore
   its optional `eechelonsapi` dependency (its `neoforge.mods.toml` has a `TODO port` for it).
