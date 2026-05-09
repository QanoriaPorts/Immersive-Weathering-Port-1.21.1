# V1.0.0

Initial Fabric 1.21.1 release.

Built on top of the same V1.0.2 baseline as the NeoForge port — every
runtime fix that landed there (recipe ingredient migration, tag dir
plural→singular rename, soil placement family extensions, weeds growth,
ice sickle attributes, channeling-on-fulgurite, leaf-decay particle
networking, hanging-roots wall placement, `c:bricks` tag namespace) is
included.

## Fabric-specific notes

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

## Build environment

- Minecraft `1.21.1`
- Fabric Loader `>=0.16.9`
- Fabric API `0.115.0+1.21.1`
- Java `21`
- Architectury Loom `1.9-SNAPSHOT`
- Architectury API `13.0.8`
- Moonlight `1.21-2.29.33`
