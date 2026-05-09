# Port Notes — Immersive Weathering 1.20.1 Forge → 1.21.1 Fabric

A Fabric 1.21.1 port of [Immersive Weathering](https://github.com/Ordana/immersive-weathering)
from Minecraft 1.20.1 / Forge. Licensed under the same LGPL-3.0 as the
upstream project; original authors (Ordana, MehVahdJukaar, Keybounce)
credited per LGPL §5(a).

**Upstream baseline:** `1.20.1-2.0.5` (commit imported as `vendor:` in git log).
**Port version:** `1.0.1` (hotfix, tracks NeoForge port V1.0.4 content baseline).

## Status

`./gradlew :fabric:build` produces the Fabric jar at
`fabric/build/libs/immersive_weathering-V1.0.1-fabric-beta.jar`.

The runtime baseline is the V1.0.4 ship from the NeoForge port - every
behavioural fix landed there is preserved through Architectury
common-mode, since this port shares the same `common/` source tree.

## V1.0.1 changes (V1.0.0 -> V1.0.1)

Hotfix release. One bug fix forwarded from NeoForge port V1.0.4
(originally contributed by Succubyte via PR #1 on the NeoForge port).

- **`fix(loot-tables)`**: 60 IW block loot tables had their Silk Touch
  predicates in the pre-1.20.5 enchantment-array shape, which 1.21.1
  silently fails to match. Result: every block that should have given
  a different drop with Silk Touch was always giving the no-silk-touch
  fallback regardless of tool. All 60 files migrated to the 1.21+
  component-based predicate shape. Affected categories: leaf piles
  (10), frost / frosty glass (3), snowy stone family (16), grassy soil
  family (5), cracked / charred / sandy / soot / icicle / thin ice /
  weeds / waxed iron variants (the remainder). Verified in-game on the
  NeoForge V1.0.4 build before forwarding to Fabric.

## Loader translation (NeoForge → Fabric)

The common module is loader-agnostic and unchanged. Loader-specific
divergences live in `fabric/src/main/java/.../fabric/`:

- **Per-item furnace burn times.** NeoForge V1.0.2 used the
  `data/neoforge/data_maps/item/furnace_fuels.json` data-map. Fabric
  has no equivalent built-in data-map system, so `regBurnableBlockItem`
  populates a `LinkedHashMap<Supplier<? extends Item>, Integer>`
  collector at registration time, and `ImmersiveWeatheringFabric.onSetup`
  drains it into `FuelRegistry.INSTANCE` once registration is complete.
  Burn times: charred log 1600, planks 400, slab/stairs/fence/fence_gate 200.
- **Biome feature additions.** NeoForge uses `biome_modifier` JSON files
  in `data/<namespace>/neoforge/biome_modifier/...`. Fabric uses the
  programmatic `BiomeModifications.addFeature(...)` API. The common
  `IWPlatformStuff.addFeatureToBiome(...)` is implemented on Fabric
  through the latter; same 13 placed features wired to the same biome
  tags as upstream.
- **Vanilla `minecraft:hanging_roots` wall placement.** Restored via
  the fabric `ItemsMixin`, which intercepts `Items.registerBlock(Block)`
  and substitutes a `CeilingAndWallBlockItem` for `Blocks.HANGING_ROOTS`.
  The common `ModEvents.onBlockCLicked` interaction chain (added during
  NeoForge V1.0.2 because NeoForge `RegisterEvent` couldn't replace the
  registration) is also active and cooperates without double-placement —
  it returns SUCCESS for wall, PASS for ceiling, and the ceiling case
  falls through to `CeilingAndWallBlockItem.useOn`.
- **Networking.** Continues to use Moonlight's
  `NetworkHelper.addNetworkRegistration` — the cross-loader abstraction
  routes to Fabric's networking pipeline at runtime via Moonlight-Fabric.
- **Access widening.** `common/src/main/resources/immersive_weathering.accesswidener`
  is shared between common and fabric; Fabric Loom injects it through
  `loom.accessWidenerPath` and `remapJar.injectAccessWidener`. NeoForge's
  `META-INF/accesstransformer.cfg` is dropped along with the rest of the
  NeoForge module.

## Behavioral changes vs. upstream 1.20.1

The remaining intentional divergence after the V1.0.2 baseline is
inherited from the NeoForge port:

- **Charred projectile interaction is a no-op stub.** The
  `Charred.interactWithProjectile(level, state, projectile, pos)` method
  is called by every `Charred*Block.onProjectileHit` and
  `entityInside`, but no implementation exists anywhere in the upstream
  source — the 1.18.2 multiloader, 1.19.2 multiloader, 1.20.0
  multiloader, and Inferno branches were all checked, and none of them
  define a body. The port retains a no-op default so the call sites
  compile; charred blocks therefore ignore projectile hits, matching
  what an upstream 2.0.5 build would do at runtime.

## License & attribution

Per LGPL-3.0 §5, this fork:
- Retains the original LGPL-3.0 license.
- Marks all modifications via the git history (every commit after the
  initial `vendor:` baseline is a port modification).
- Preserves the upstream copyright notice in `LICENSE`.
- Credits the original authors: Ordana, MehVahdJukaar, Keybounce.

## Build environment

- Minecraft `1.21.1`
- Fabric Loader `0.16.9`
- Fabric API `0.115.0+1.21.1`
- Java `21`
- Gradle `8.11.1`
- Architectury Loom `1.9-SNAPSHOT`
- Architectury Plugin `3.4-SNAPSHOT`
- Architectury API `13.0.8`
- Moonlight `1.21-2.29.33`

## Known follow-ups

- Charred-block projectile interaction body remains a no-op default —
  upstream never had one. If a real behaviour is desired (e.g. lit
  projectile ignites the block), it would have to be designed from
  scratch rather than ported.

## Building

```
./gradlew :fabric:build
```

The output jar appears at `fabric/build/libs/immersive_weathering-V1.0.0-fabric-beta.jar`.
