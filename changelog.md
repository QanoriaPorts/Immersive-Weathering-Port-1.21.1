# V1.0.2 (port)

Closes the remaining intentional regressions documented in V1.0.1's
PORT_NOTES, plus a small followup audit pass against the upstream
`1.20.0-multiloader` source.

- restored leaf-decay particles + sound: rewired the
  `immersive_weathering:custom_particles` payload through Moonlight 2.29.x's
  `Message` interface and registered it as a client-bound payload via
  `NetworkHelper.addNetworkRegistration`; the client-side particle/sound
  spawn moved to a sibling class so dedicated servers don't link
  `Minecraft`
- restored vanilla `minecraft:hanging_roots` wall placement on a vertical
  surface via a new entry in the `ModEvents.onBlockCLicked` interaction
  chain (NeoForge 1.21's `RegisterEvent` cannot replace existing item
  registrations the way Forge 1.20.1 did, so the upstream
  `CeilingAndWallBlockItem` registry-override path was dropped in favour
  of an event-based intercept)
- migrated `data/forge/tags/item/ingots/brick.json` →
  `data/c/tags/item/bricks.json` so IW bricks register against the
  community-standard 1.21+ tag
- restored Ice Sickle damage/attack-speed attributes that 1.21.1's
  `SwordItem(Tier, Properties)` constructor no longer wires automatically
- restored Channeling enchantment check on the Fulgurite trident hit
  (looks the enchantment up as `Holder<Enchantment>` from the level's
  registry)
- restored per-item furnace burn times via the NeoForge data-map system
  (`data_maps/item/furnace_fuels.json`)
- audit confirmed the codec dispatch chains in `AreaCondition`,
  `IPositionRuleTest`, `IFluidGenerator`, `BuiltinBlockGrowth`, and
  `BlockPropertyTest.PropPredicate` are not stubbed (PORT_NOTES had been
  inaccurate); their variant types all expose `MapCodec<T>` as 1.21
  dispatch requires
- audit confirmed `Charred.interactWithProjectile` has never had an
  implementation in any upstream branch (1.18.2 / 1.19.2 / 1.20.0
  multiloader / Inferno); the no-op default remains and is now the only
  remaining intentional divergence from upstream

# V1.0.1

Added an area-loaded check to fluid generators to prevent an unknown
blocking issue when fluids are near unloaded chunks.
