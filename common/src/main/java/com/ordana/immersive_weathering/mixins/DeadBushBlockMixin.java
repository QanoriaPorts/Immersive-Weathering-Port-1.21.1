package com.ordana.immersive_weathering.mixins;

import com.ordana.immersive_weathering.reg.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.DeadBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Lets dead bushes be placed on the IW soil family. {@code DeadBushBlock}
 * overrides {@code mayPlaceOn} from {@code BushBlock} with its own narrower
 * tag check, so {@code BushBlockMixin} never fires for dead bushes — we
 * inject directly here. Uses the IW-namespaced fallback tag so this still
 * works under modpacks where another mod replaces vanilla
 * {@code minecraft:dirt} / {@code minecraft:sand} via {@code replace: true}.
 */
@Mixin(DeadBushBlock.class)
public abstract class DeadBushBlockMixin {

    @Inject(method = "mayPlaceOn", at = @At("HEAD"), cancellable = true)
    protected void iw$mayPlaceOnIwSoil(BlockState state, BlockGetter level, BlockPos pos,
                                       CallbackInfoReturnable<Boolean> cir) {
        if (state.is(ModTags.IW_SOIL_PLACEABLE)) {
            cir.setReturnValue(true);
        }
    }
}
