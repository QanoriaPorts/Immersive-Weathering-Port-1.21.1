package com.ordana.immersive_weathering.mixins;

import com.ordana.immersive_weathering.reg.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Lets bamboo stalks survive on the IW soil family. Vanilla
 * {@code BambooStalkBlock.canSurvive} checks {@code BlockTags.BAMBOO_PLANTABLE_ON};
 * we extend with a HEAD inject keyed off the IW-namespaced fallback tag so
 * placement is robust against third-party {@code replace: true} overrides
 * of vanilla tags.
 */
@Mixin(BambooStalkBlock.class)
public abstract class BambooStalkBlockMixin {

    @Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
    protected void iw$canSurviveOnIwSoil(BlockState state, LevelReader level, BlockPos pos,
                                         CallbackInfoReturnable<Boolean> cir) {
        if (level.getBlockState(pos.below()).is(ModTags.IW_SOIL_PLACEABLE)) {
            cir.setReturnValue(true);
        }
    }
}
