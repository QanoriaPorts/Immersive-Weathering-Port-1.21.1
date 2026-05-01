package com.ordana.immersive_weathering.neoforge;

import com.ordana.immersive_weathering.ImmersiveWeathering;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Authors: MehVahdJukaar, Ordana, Keybounce
 * 1.21.1 / NeoForge port: see PORT_NOTES.md
 *
 * Note: the upstream 1.20.1 entry point overrode the vanilla
 * {@code minecraft:hanging_roots} item registration with a custom
 * {@link CeilingAndWallBlockItem}. NeoForge 1.21.1's
 * {@code RegisterEvent} does not support overriding existing
 * registry entries, so that override has been removed here. Wall
 * placement of vanilla hanging roots is therefore not provided by
 * this port; the {@code CeilingAndWallBlockItem} class is retained
 * for potential reuse via mixin or for mod-namespaced blocks.
 */
@Mod(ImmersiveWeathering.MOD_ID)
public class ImmersiveWeatheringNeoForge {
    public static final String MOD_ID = ImmersiveWeathering.MOD_ID;

    public ImmersiveWeatheringNeoForge(IEventBus modBus) {
        ImmersiveWeathering.commonInit();
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        var ret = com.ordana.immersive_weathering.events.ModEvents.onBlockCLicked(event.getItemStack(),
                event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec());
        if (ret != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(ret);
        }
    }
}
