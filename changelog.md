# V1.0.0 (beta)

Initial Fabric 1.21.1 release of the QanoriaPorts Immersive Weathering
port, branched from the NeoForge port's V1.0.3 baseline. Both loaders
share the same `common/` source tree, so every runtime fix that landed
across V1.0.0 → V1.0.3 on the NeoForge side (recipe ingredient
migration, tag dir plural→singular rename, soil placement family
extensions, weeds growth, ice sickle attributes,
channeling-on-fulgurite, leaf-decay particle networking, hanging-roots
wall placement, `c:bricks` tag namespace, and the V1.0.3
axe-on-blocks-without-items crash hotfix) is included verbatim. From
here on the two loaders track each other content-wise but version
their own lifecycles independently — a Fabric-only hotfix bumps the
Fabric line without touching NeoForge's number, and vice versa.

## Fabric-specific loader translation

- **Per-item furnace burn times** are wired through Fabric's
  `FuelRegistry`, not a data-map. `regBurnableBlockItem` collects the
  item supplier + burn time pairs at registration; once mod init has
  finished, `ImmersiveWeatheringFabric.onSetup` drains the collector
  into `FuelRegistry.INSTANCE`. Same six entries as upstream: charred
  log 1600, planks 400, slab/stairs/fence/fence_gate 200.
- **Biome features** are added programmatically via Fabric's
  `BiomeModifications.addFeature` rather than the NeoForge
  `biome_modifier` JSON files. The feature placement keys (`icicles`,
  `frost_patch`, `loam`, `silt`, `silt_aquifer`, `sandy_dirt`,
  `earthen_clay`, `permafrost`, `dry_lakebed`, `dry_lakebed_large`,
  `ivy_patch`, `dune_grass_patch`, `moss_patch`) are the same; only the
  registration mechanism differs.
- **Vanilla `minecraft:hanging_roots` wall placement** uses both the
  fabric `ItemsMixin` (replaces the registered `BlockItem` with
  `CeilingAndWallBlockItem`) and the `ModEvents.onBlockCLicked` chain
  inherited from common. Both paths cooperate without double-placement —
  the use-block callback returns SUCCESS for wall, PASS for ceiling, and
  ceiling falls through to the `CeilingAndWallBlockItem.useOn` flow.
- **`forge:ingots/brick` tag** is shipped as `c:bricks` (the
  community-standard 1.21+ namespace).
- **`HumanoidArmorLayer` mixin** updated for 1.21.1's signature change
  on `renderArmorPiece` — `getArmorFoilBuffer` dropped its `noEntity`
  flag, and `Model.renderToBuffer` now packs RGBA into a single ARGB
  int. Flower Crown render layer matches upstream.

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
