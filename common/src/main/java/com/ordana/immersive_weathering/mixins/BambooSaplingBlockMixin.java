package com.ordana.immersive_weathering.mixins;

import com.ordana.immersive_weathering.reg.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BambooSaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Companion to {@link BambooStalkBlockMixin}: bamboo SAPLINGS (the small
 * pre-stalk variant) are a separate block class with their own
 * {@code canSurvive} check. Same fallback rule.
 */
@Mixin(BambooSaplingBlock.class)
public abstract class BambooSaplingBlockMixin {

    @Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
    protected void iw$canSurviveOnIwSoil(BlockState state, LevelReader level, BlockPos pos,
                                         CallbackInfoReturnable<Boolean> cir) {
        if (level.getBlockState(pos.below()).is(ModTags.IW_SOIL_PLACEABLE)) {
            cir.setReturnValue(true);
        }
    }
}
