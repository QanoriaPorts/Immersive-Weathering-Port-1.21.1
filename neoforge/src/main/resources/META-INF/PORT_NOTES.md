# Port Notes — Immersive Weathering 1.20.1 Forge → 1.21.1 NeoForge

A NeoForge 1.21.1 port of [Immersive Weathering](https://github.com/Ordana/immersive-weathering)
from Minecraft 1.20.1 / Forge. Licensed under the same LGPL-3.0 as the
upstream project; original authors (Ordana, MehVahdJukaar, Keybounce)
credited per LGPL §5(a).

**Upstream baseline:** `1.20.1-2.0.5` (commit imported as `vendor:` in git log).
**Port version:** `1.0.3` (beta).

## Status

✅ **`./gradlew :neoforge:build` succeeds.** The mod jar is produced at
`neoforge/build/libs/Immersive-weathering-V1.0.3-Beta.jar`.

✅ **Verified in a running game** (both standalone and inside a 95-mod
modpack). All known runtime regressions surfaced during testing have been
fixed:

- Iron rusting on land + in water
- Moss spread on stone, cobblestone, brick, and stone-brick families
- Sapling, flower, and bush placement on Rooted Grass, Earthen Clay,
  Sandy Dirt, Silt, Permafrost, and the grassy variants
- Dead bush, bamboo (stalk + sapling), sugar cane placement on IW soils
- Weeds (crop) growth ticks (NeoForge `CropBlock.getGrowthSpeed`
  signature divergence handled)
- All 349 IW recipes load cleanly under the 1.21 ingredient JSON format

## V1.0.3 changes (V1.0.2 → V1.0.3)

Hotfix release. One crash fix; nothing else changed.

- **`fix(axe-on-blocks-without-items)`**: defensive try/catch around
  Moonlight 2.29's `BlockSetAPI.getBlockTypeOf(block, WoodType.class)`.
  The Moonlight call throws `IllegalStateException` when the queried
  block has no registered Item — and some mods ship blocks with no
  inventory item (e.g. Bountiful Fares' `bountifulfares:hanging_lemon`,
  a fruit block placed by tree generation only). Right-clicking such a
  block with an axe ran IW's `axeStripping` handler, which forwarded
  through `WeatheringHelper.getBarkToStrip` into the throwing Moonlight
  call and crashed the client. Added a private
  `safeGetWoodType(Block)` helper in `WeatheringHelper` that wraps the
  Moonlight call in a try/catch and returns `null` on the throw. All
  three call sites (`getBarkToStrip`, `getBarkForStrippedLog`,
  `getWoodFromLog`) route through it. Vanilla axe behaviour takes over
  for any block IW doesn't recognise as a log/wood family.

## Known external issues (not IW)

- **Bountiful Fares 3.0.8 + NeoForge 21.1.228**: Bountiful Fares'
  `PicketsBlock.getCollisionShape` throws `IncompatibleClassChangeError`
  against vanilla `IndirectMerger.forMergedIndexes` during world creation
  (`Blocks.rebuildCache` → `Shapes.or` chain). Reproduces with IW
  disabled; not an IW × Bountiful Fares interaction. Workaround for
  downstream users: update Bountiful Fares to a build compiled against
  the same NeoForge minor or downgrade NeoForge to ≤ 21.1.219.

## V1.0.2 changes (V1.0.1 → V1.0.2)

Followup pass closing the remaining shippable divergences from upstream
1.20.1-2.0.5. The first three items came from the V1.0.1 → V1.0.2 pre-pass
(fuels / ice sickle / channeling); the last three come from the second
V1.0.2 pass that audited the rest of the file against the upstream
`1.20.0-multiloader` branch on
[github.com/Silversmith-Mods/Immersive-Weathering](https://github.com/Silversmith-Mods/Immersive-Weathering).

- **`fix(fuels)`**: restored per-item furnace burn times via the NeoForge
  data-map system. Added `data/immersive_weathering/data_maps/item/furnace_fuels.json`
  with the upstream burn times: charred log 1600, charred planks 400,
  charred slab/stairs/fence/fence_gate 200. Note: PORT_NOTES previously
  referenced `neoforge/fuels.json` — that path was wrong; the correct
  NeoForge 1.21.1 location is `data_maps/item/furnace_fuels.json`.
- **`fix(ice-sickle)`**: restored Ice Sickle damage (+5) and attack speed
  (-1.0) attributes. NeoForge 1.21.1's `SwordItem(Tier, Properties)`
  constructor only attaches the `TOOL` data component (cobweb-mining +
  sword_efficient) — it does NOT auto-set attribute modifiers — so we
  pre-bake them via `Properties.attributes(SwordItem.createAttributes(...))`
  inside `IceSickleItem`'s constructor before delegating to super.
- **`fix(channeling)`**: restored Channeling enchantment check on the
  trident in `FulguriteBlock.onProjectileHit`. Looks up the Channeling
  enchantment via the level's registry as a `Holder<Enchantment>`, then
  calls `EnchantmentHelper.getItemEnchantmentLevel(channeling, trident.getWeaponItem())`.
  Lightning now triggers only on Channeling-enchanted trident hits during
  thunderstorms, matching upstream behavior.
- **`fix(particles)`**: restored client-side leaf-decay particles and
  sound. `SendCustomParticlesPacket` now implements Moonlight 2.29.x's
  `Message` interface (which extends vanilla
  `CustomPacketPayload`), is registered through `NetworkHelper.addNetworkRegistration`
  in `NetworkHandler.init()`, and dispatches via
  `NetworkHelper.sendToAllClientPlayersInRange`. The client-side particle
  + sound logic that used to live in the packet's `clientStuff()` was
  moved to a sibling `ClientHandler` class so dedicated servers don't
  link `Minecraft`. The new payload id is `immersive_weathering:custom_particles`.
- **`fix(hanging-roots-wall)`**: restored vanilla `minecraft:hanging_roots`
  wall placement on a vertical surface. NeoForge 1.21's `RegisterEvent`
  cannot replace an existing item registration the way Forge 1.20.1 did
  via `event.getForgeRegistry().register(...)`, so the `CeilingAndWallBlockItem`
  override approach won't work. Instead, a new `hangingRootsWallPlacement`
  entry was added to the `ModEvents.onBlockCLicked` interaction chain;
  it intercepts right-clicks holding `Items.HANGING_ROOTS`, runs the
  upstream `getNearestLookingDirections` selection logic against
  `HANGING_ROOTS_WALL.getStateForPlacement`, and places the wall block
  with full sound, gameevent, criterion, and stack-shrink behaviour.
  Ceiling clicks fall through to vanilla unchanged.
- **`fix(brick-tag-namespace)`**: migrated
  `data/forge/tags/item/ingots/brick.json` → `data/c/tags/item/bricks.json`.
  NeoForge 1.21.1 still honors the legacy `forge:` namespace for
  back-compat, but `c:bricks` is the community-standard tag in 1.21+
  and is the namespace that downstream mods register against.

Discovery during this pass: Icicle and Ice Sickle food were *already*
working in V1.0.1 — they're wired through `Properties.food(ModFoods.ICICLE)`
at registration. PORT_NOTES had erroneously listed them as broken.

Discovery during the second pass: the codec dispatch chains in
`AreaCondition`, `IPositionRuleTest`, `IFluidGenerator`, `BuiltinBlockGrowth`,
and `BlockPropertyTest.PropPredicate` had all already been correctly
migrated to `MapCodec` during the V1.0.0 build-fix work — they are not
stubbed. PORT_NOTES claiming they were "always-error / always-empty
codecs" was inaccurate. The variant types (`AreaCheck`, `NeighborCheck`,
`SelfFluidGenerator`, `OtherFluidGenerator`, `BurnMossGenerator`,
`AndTest`, `OrTest`, `NandTest`, etc.) all expose `MapCodec<T>` as
required by 1.21's dispatch contract and are registered against their
respective `Mod*` registry maps.

Confirmed not portable in V1.0.2: the upstream `1.20.0-multiloader`
source has no implementation of `Charred.interactWithProjectile` —
all six `Charred*Block` subclasses call it from `onProjectileHit` /
`entityInside`, but the interface definition was never present in any
checked-in revision (1.18.2 / 1.19.2 / 1.20.0 multiloader / Inferno
branches were all checked). The port retains a no-op default.

## V1.0.1 changes (V1.0.0 → V1.0.1)

- **`fix(weeds)`**: replaced `CropBlock.getGrowthSpeed(this, level, pos)`
  with an inlined fixed growth rate. NeoForge 21.1 patches that method's
  signature (`Block` → `BlockState`) but Architectury common-mode compiles
  against unpatched vanilla, so the call crashed at first chunk tick with
  `NoSuchMethodError`. Behavior matches vanilla's default growth speed
  (1.0) on a clean tile; differs from upstream only on hydrated farmland
  where vanilla would have given a small speed bonus.
- **`fix(soils-bush)`**: added IW-namespaced `iw_soil_placeable` tag and
  fallback check in `BushBlockMixin`. Prevents sapling/flower/bush
  placement from breaking when another mod ships a `replace: true`
  override of vanilla `minecraft:dirt` (e.g. Still Life mod does this in
  the wild).
- **`fix(soils-other)`**: added `DeadBushBlockMixin`,
  `BambooStalkBlockMixin`, `BambooSaplingBlockMixin`, and extended
  `SugarCaneBlockMixin` to allow placement on the IW soil family using
  the same `iw_soil_placeable` fallback. Sugar cane requires water
  orthogonally adjacent below, mirroring vanilla. Bamboo additionally
  required adding the IW soils to `minecraft:bamboo_plantable_on` because
  `BambooStalkBlock.getStateForPlacement` checks that tag *before*
  `canSurvive`.
- **`fix(recipes)`**: migrated all 349 IW recipe JSONs from the 1.20.x
  ingredient string-shorthand to the 1.21.x explicit Ingredient object
  form. One-shot script preserved at repo root as `fix_recipes.py` for
  documentation. Affects shaped recipes (`key`), shapeless recipes
  (`ingredients`), and stonecutting/smelting/smoking/blasting/campfire
  recipes (top-level `ingredient`). Idempotent — already-correct objects
  are passed through unchanged.

## Runtime fixes applied during testing

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

The remaining intentional divergence after the V1.0.2 pass:

- **Charred projectile interaction is a no-op stub.** The
  `Charred.interactWithProjectile(level, state, projectile, pos)` method
  is called by every `Charred*Block.onProjectileHit` and
  `entityInside`, but no implementation of it exists anywhere in the
  upstream source — the 1.18.2 multiloader, 1.19.2 multiloader, 1.20.0
  multiloader, and Inferno branches were all checked, and none of them
  define a body. The port retains a no-op default so the call sites
  compile; charred blocks therefore ignore projectile hits, matching
  what an upstream 2.0.5 build would do at runtime.

### Resolved in V1.0.2

- ~~Per-item fuel times not registered in code.~~ Restored via
  `data/immersive_weathering/data_maps/item/furnace_fuels.json`.
- ~~Custom sword damage/speed dropped on `IceSickleItem`.~~ Restored
  via `Properties.attributes(SwordItem.createAttributes(...))` inside
  the constructor.
- ~~Trident channeling check simplified.~~ Restored by looking up the
  Channeling enchantment as `Holder<Enchantment>` from the level's
  registry and gating on `EnchantmentHelper.getItemEnchantmentLevel`.
- ~~Edible item flag dropped on `IcicleItem` / `IceSickleItem`.~~ This
  was a documentation error — both items had been wired with
  `Properties.food(ModFoods.ICICLE)` at registration since V1.0.0. The
  PORT_NOTES claim that they were broken was inaccurate.
- ~~Vanilla `minecraft:hanging_roots` wall placement removed.~~ Restored
  via the `hangingRootsWallPlacement` entry in the `ModEvents.onBlockCLicked`
  interaction chain. The `CeilingAndWallBlockItem` class in
  `neoforge/src/main/java/.../neoforge/` is no longer wired in but is
  preserved for reference; the actual wall-placement decision now happens
  inside `ModEvents`.
- ~~Custom particle network packets are no-op.~~ Restored via Moonlight
  2.29.x's `Message` interface. `SendCustomParticlesPacket` now serializes
  through `RegistryFriendlyByteBuf` and is registered as a client-bound
  payload via `NetworkHelper.addNetworkRegistration` in
  `NetworkHandler.init()`. Leaf-decay particles + sounds fire on clients
  again.
- ~~Codec dispatch chains stubbed.~~ Verification pass found these to be
  already correctly migrated to `MapCodec` in V1.0.0. The variant types
  (`AreaCheck`, `NeighborCheck`, `SelfFluidGenerator`,
  `OtherFluidGenerator`, `BurnMossGenerator`, plus the
  `IPositionRuleTest` family) all expose `MapCodec<T>`, and dispatch in
  `AreaCondition`, `IFluidGenerator`, `IPositionRuleTest`, and
  `BuiltinBlockGrowth` correctly hands those map codecs to
  `Codec.dispatch` / `partialDispatch`. The PORT_NOTES claim of
  always-error / always-empty codecs was inaccurate.
- ~~`forge:ingots/brick` tag.~~ Migrated to `c:bricks` (community-standard
  1.21+ namespace).

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

- The `fabric/` module's mixins target 1.20.1 vanilla method signatures.
  They may need adjustment for 1.21.1 (e.g. `FarmBlock.fallOn` parameters).
  This port was scoped to NeoForge; the fabric module is preserved for
  future updating but `:fabric:build` was not verified.
- Confirm `BlockColors`/`ItemColors` accessor mixin field names against
  current 1.21.1 mappings — those may have shifted to `IdMapper<...>`.
- Charred-block projectile interaction body remains a no-op default —
  upstream never had one. If a real behaviour is desired (e.g. lit
  projectile ignites the block), it would have to be designed from
  scratch rather than ported.

## Building

```
./gradlew :neoforge:build
```

The output jar appears at `neoforge/build/libs/immersive_weathering-…-neoforge.jar`.
