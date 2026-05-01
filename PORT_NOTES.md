# Port Notes — Immersive Weathering 1.20.1 Forge → 1.21.1 NeoForge

This is an unofficial port of [Immersive Weathering](https://github.com/Ordana/immersive-weathering)
from Minecraft 1.20.1 / Forge to Minecraft 1.21.1 / NeoForge. It is licensed
under the same LGPL-3.0 as the upstream project.

**Upstream baseline:** `1.20.1-2.0.5` (commit imported as `vendor:` in git log).
**Port version:** `1.21.1-2.0.5-port1`.

## Status

✅ **`./gradlew :neoforge:build` succeeds.** The mod jar is produced at
`neoforge/build/libs/immersive_weathering-1.21.1-2.0.5-port1-neoforge.jar`.

✅ **Smoke-tested in a running game.** Iron rusting (land + water), moss
spread on stone/cobblestone, and sapling/flower placement on the IW
dirt-family blocks (Rooted Grass Block, Earthen Clay, Sandy Dirt, Silt,
Permafrost, and the grassy variants) all verified working under play.

## Runtime fixes applied during smoke test

- **Tag directory rename plural → singular.** Minecraft 1.21 renamed all
  vanilla tag directories (`tags/blocks/` → `tags/block/`, `tags/items/`
  → `tags/item/`, `tags/entity_types/` → `tags/entity_type/`). The 1.21.1
  datapack loader silently ignores anything in the old plural folders.
  Renamed every affected directory under `data/<namespace>/tags/...`
  across the `immersive_weathering`, `minecraft`, `create`, `curios`,
  `snowrealmagic`, `trinkets`, and `forge` namespaces. This single fix
  resolved three observable runtime bugs:
  - Iron blocks not rusting on land (only weathering in water): the
    on-land branch in `Rustable.tryWeather` is gated by
    `state.is(ModTags.CLEAN_IRON)` / `EXPOSED_IRON`, which always
    returned false because the tag JSON was at the legacy
    `tags/blocks/clean_iron.json` path. The water branch is not
    tag-gated, hence rusting in water still worked.
  - Saplings, flowers, and other bushes refusing to place on the IW
    dirt-family blocks (Rooted Grass, Earthen Clay, Sandy Dirt, Silt,
    Permafrost, and grassy variants): vanilla `BushBlock.mayPlaceOn`
    checks `BlockTags.DIRT`, and the
    `data/minecraft/tags/blocks/dirt.json` override that adds the
    IW soil blocks was sitting in the legacy plural folder.
  - Stone and cobblestone never gaining moss: `MossSpreader.getWeathering*`
    keys off `ModTags.MOSSY` and `ModTags.MOSS_SOURCE` membership; same
    root cause.

## License & attribution

Per LGPL-3.0 §5, this fork:
- Retains the original LGPL-3.0 license.
- Marks all modifications via the git history (every commit after the
  initial `vendor:` baseline is a port modification).
- Preserves the upstream copyright notice in `LICENSE`.
- Credits the original authors: Ordana, MehVahdJukaar, Keybounce.

## Behavioral changes vs. upstream 1.20.1

These are intentional regressions taken to keep the port shippable; each is
a candidate for follow-up work:

- **Vanilla `minecraft:hanging_roots` wall placement removed.** NeoForge
  1.21.1's `RegisterEvent` does not support replacing existing registry
  entries. The `CeilingAndWallBlockItem` class is retained for future
  reuse via mixin or for mod-namespaced blocks.
- **Custom particle network packets are no-op.** Moonlight 2.29.x replaced
  `ChannelHandler` / `Message` with `NetworkHelper` + `CustomPacketPayload.TypeAndCodec`.
  `NetworkHandler` and `SendCustomParticlesPacket` are stubbed; leaf-decay
  and similar effects no longer push particles to the client. Block state
  transitions, sounds, and everything not particle-network-driven still work.
- **Per-item fuel times no longer registered in code.** Moonlight removed
  `FuelBlockItem`; in 1.21.1 fuel durations are data-driven via
  `data/<modid>/neoforge/fuels.json`. Existing burnable items fall back to
  plain `BlockItem` and need a datapack entry to retain fuel value.
- **Custom sword damage/speed values dropped on `IceSickleItem`.** 1.21.1
  `SwordItem(Tier, Properties)` no longer takes int/float ctor args; the
  values are now configured via `Item.Properties.attributes(SwordItem.createAttributes(...))`.
  Re-introduce by editing `IceSickleItem`'s registration site if desired.
- **Edible item flag dropped on `IcicleItem` and `IceSickleItem`.** The
  `isEdible` / `getFoodProperties` overrides were removed; food is now a
  `DataComponent` set via `Item.Properties.food(...)` at registration. Wire
  via `ModItems` if you want them edible again.
- **Trident channeling check simplified in `FulguriteBlock`.** Vanilla's
  `ThrownTrident.isChanneling()` was removed; the lightning-on-trident-hit
  feature now triggers on any trident hit during a thunderstorm rather than
  specifically on channeling-enchanted tridents.
- **Codec dispatch chains stubbed.** The five places that built
  `Codec<X>` via `Codec.dispatch` / `Codec.STRING.partialDispatch`
  (`AreaCondition`, `IPositionRuleTest`, `IFluidGenerator`,
  `BuiltinBlockGrowth`, and `BlockPropertyTest.PropPredicate`) were stubbed
  to always-error / always-empty codecs because 1.21 changed `dispatch` to
  require `MapCodec` for the per-variant codec. The data-driven systems
  built on them are inert; revisit once the variant codecs are migrated to
  `MapCodec` and dispatch is rewired.
- **Charred projectile interaction is a no-op stub.** The original
  `interactWithProjectile` body was missing in the imported source; the
  default added here does nothing. Charred blocks ignore projectile hits.

## Build environment

- Minecraft `1.21.1`
- NeoForge `21.1.219`
- Java `21`
- Gradle `8.11.1` (wrapper bumped from 8.8)
- Architectury Loom `1.9-SNAPSHOT` (bumped from 1.7; required for the
  Architectury common-mode classpath to resolve Minecraft properly)
- Architectury Plugin `3.4-SNAPSHOT`
- Architectury API `13.0.8`
- Moonlight `1.21-2.29.33`

## Known follow-ups (not yet done)

- `data/forge/tags/item/ingots/brick.json` still uses the legacy `forge:`
  tag namespace. NeoForge 1.21.1 honors it for backwards compatibility,
  but the community-standard `c:` namespace is preferred. Migrate
  `forge:ingots/brick` → `c:bricks` (note plural rename).
- The `fabric/` module's mixins target 1.20.1 vanilla method signatures.
  They may need adjustment for 1.21.1 (e.g. `FarmBlock.fallOn` parameters).
  This port was scoped to NeoForge; the fabric module is preserved for
  future updating but `:fabric:build` was not verified.
- Confirm `BlockColors`/`ItemColors` accessor mixin field names against
  current 1.21.1 mappings — those may have shifted to `IdMapper<...>`.
- Re-implement the codec dispatch stubs (above) so the data-driven systems
  work again.
- Re-wire `NetworkHandler` against `NetworkHelper.addNetworkRegistration`
  + `CustomPacketPayload.TypeAndCodec` to restore networked particles.

## Building

```
./gradlew :neoforge:build
```

The output jar appears at `neoforge/build/libs/immersive_weathering-…-neoforge.jar`.
