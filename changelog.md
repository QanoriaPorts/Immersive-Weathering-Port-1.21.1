# V1.0.5 (beta)

Hotfix on top of V1.0.4. One visual fix for heavy leaf pile textures
on small-palette modded leaves.

## Heavy leaf pile texture fix

- **Pear leaf pile (and any other small-palette modded leaves) no
  longer turns blue at high layer counts.** IW's
  `ClientDynamicResourcesHandler` generates a "heavy" texture variant
  for leaf piles at LAYERS 5-8 by extracting the leaves' palette and
  filling transparent pixels with the darkest color, giving thick
  piles a shadowed interior. When the source palette has more than
  five copies of its darkest color, the algorithm calls
  `Palette.increaseDown()` to *extrapolate* an even-darker synthetic
  color in HCL space and use that as the fill. For leaves with a tiny,
  low-chroma palette - e.g. No Man's Land's `pear_fruit_leaves`, which
  shares the `autumnal_oak_leaves.png` texture (5 brown colors only) -
  the HCL extrapolation lands outside the source gamut and clamps into
  a cold-hue synthetic, flooding the transparent pixels with blue.
- **Fix**: guard the `increaseDown()` call with a palette-size check
  (`targetPalette.size() > 6`). For small palettes, fall back to the
  real darkest entry, which fills transparency with the actual leaf
  shadow color and stays in-gamut. Vanilla oak / birch / jungle / etc.
  leaves have richer palettes and remain unchanged.

# V1.0.4 (beta)

Hotfix on top of V1.0.3. Loot-table predicate format migration
contributed by Succubyte (PR #1).

## Loot table fix

- **Silk Touch detection on IW blocks now works on 1.21.1.** 60 IW
  block loot tables used the pre-1.20.5 enchantment predicate shape
  (`"enchantments": [{ "enchantment": "minecraft:silk_touch" }]`),
  which 1.21.1 silently fails to match. Result: every block that
  should have dropped a different item with Silk Touch (leaf piles,
  frosty glass, snowy stone family, grassy soil family, charred
  blocks, thin ice, soot, frost, icicles, and more) was always
  giving the no-silk-touch loot regardless of tool. Migrated all 60
  files to the 1.21+ component-based predicate shape
  (`"components": { "minecraft:enchantments": { "levels": { "minecraft:silk_touch": 1 } } }`).
  Verified in-game across leaf piles, grassy permafrost, frosty
  glass, and snowy stone bricks.

Thanks to Succubyte for the catch and the fix.

# V1.0.3 (beta)

Hotfix on top of V1.0.2. One crash fix surfaced after the V1.0.2 release.

## Crash fix

- **Right-clicking a third-party "block-without-item" with an axe no
  longer crashes.** IW's `axeStripping` event handler was calling
  `WeatheringHelper.getBarkToStrip(state)`, which forwarded to
  Moonlight's `BlockSetAPI.getBlockTypeOf(block, WoodType.class)`.
  Moonlight 2.29 throws `IllegalStateException` when the queried block
  has no registered Item — and some mods ship blocks with no inventory
  item (e.g. Bountiful Fares' `bountifulfares:hanging_lemon`, a fruit
  block placed by tree generation only). Trying to right-click the
  fruit with an iron axe crashed the client.
- **Fix**: added a private `safeGetWoodType(Block)` helper in
  `WeatheringHelper` that wraps the Moonlight call in a try/catch and
  returns `null` on the throw. All three call sites
  (`getBarkToStrip`, `getBarkForStrippedLog`, `getWoodFromLog`) now
  route through it. Vanilla axe behaviour takes over for blocks IW
  doesn't recognise as a log/wood family — including any third-party
  block that doesn't have a corresponding inventory item.

# V1.0.2 (beta)

Closes the remaining intentional regressions from V1.0.1. After V1.0.2 the
port has exactly one divergence from upstream `1.20.1-2.0.5`, and that one
was already broken upstream.

All six fixes were verified in-game on a 95-mod NeoForge 1.21.1 modpack.

## Restored upstream behaviour

- **Per-item furnace burn times.** Charred log → 1600t, charred planks
  → 400t, charred slab/stairs/fence/fence_gate → 200t. The data-map JSON
  was being silently ignored before this build because it was at the
  wrong namespace path (`data/immersive_weathering/data_maps/item/...`);
  NeoForge resolves built-in data maps by their map ID, so for
  `neoforge:furnace_fuels` the file has to live at
  `data/neoforge/data_maps/item/furnace_fuels.json`. Moved.
- **Ice Sickle damage / attack speed.** +5 damage, -1.0 attack speed.
  NeoForge 1.21.1's `SwordItem(Tier, Properties)` only auto-attaches
  the `TOOL` data component, so the modifiers are pre-baked on
  `Properties` via `Properties.attributes(SwordItem.createAttributes(
  tier, damageBonus, attackSpeed))` before the constructor delegates
  to super.
- **Trident Channeling on Fulgurite.** Channeling lookup ported to the
  data-driven 1.21 form: enchantment fetched as
  `Holder<Enchantment>` via the level's `RegistryAccess`, then
  `EnchantmentHelper.getItemEnchantmentLevel(channeling,
  trident.getWeaponItem())`. Lightning fires only on Channeling-
  enchanted hits during a thunderstorm with sky access.
- **Leaf-decay particles + sound.** `SendCustomParticlesPacket` now
  implements Moonlight 2.29's `Message` interface, registered as a
  client-bound payload via `NetworkHelper.addNetworkRegistration` in
  `NetworkHandler.init()`. Client-side particle/sound logic moved to a
  sibling `ClientHandler` class so dedicated servers don't link
  `Minecraft`.
- **Vanilla `minecraft:hanging_roots` wall placement.** NeoForge 1.21's
  `RegisterEvent` cannot replace existing item registrations the way
  Forge 1.20.1 did, so the upstream `CeilingAndWallBlockItem`
  registry-override is replaced with a `hangingRootsWallPlacement`
  entry in the `ModEvents.onBlockCLicked` interaction chain. Wall
  placement runs the same `getNearestLookingDirections` selection logic
  as upstream; ceiling clicks fall through to vanilla unchanged.
- **Brick tag namespace.** Migrated
  `data/forge/tags/item/ingots/brick.json` →
  `data/c/tags/item/bricks.json` (community-standard 1.21+ tag).

## Documentation corrections

PORT_NOTES had two inaccurate "intentional regression" entries. Both
audited against upstream and corrected:

- The five codec dispatch chains (`AreaCondition`, `IPositionRuleTest`,
  `IFluidGenerator`, `BuiltinBlockGrowth`,
  `BlockPropertyTest.PropPredicate`) had already been correctly
  migrated to `MapCodec` in V1.0.0 — not stubbed.
- `Charred.interactWithProjectile` has never had an implementation
  anywhere upstream (1.18.2 / 1.19.2 / 1.20.0 multiloader / Inferno
  branches all checked). The no-op default matches upstream runtime
  behaviour and is the only remaining intentional divergence after
  V1.0.2.

# V1.0.1 (beta)

Added an area-loaded check to fluid generators to prevent a blocking
issue when fluids are near unloaded chunks.

# V1.0.0 (beta)

Initial NeoForge 1.21.1 port of [Immersive Weathering](https://github.com/Silversmith-Mods/Immersive-Weathering)
(upstream baseline `1.20.1-2.0.5`). Brings up build, recipes, tags,
weathering mechanics, and the IW soil/leaf/charred families on
NeoForge 1.21.1. See `PORT_NOTES.md` for the full migration write-up.
