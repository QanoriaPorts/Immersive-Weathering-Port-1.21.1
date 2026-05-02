package com.ordana.immersive_weathering.mixins;

import com.ordana.immersive_weathering.blocks.soil_types.EarthenClayFarmlandBlock;
import com.ordana.immersive_weathering.reg.ModBlocks;
import com.ordana.immersive_weathering.reg.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SugarCaneBlock.class)
public abstract class SugarCaneBlockMixin {

    @Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
    public void canPlaceAt(BlockState state, LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockState below = level.getBlockState(pos.below());

        // Existing upstream behavior: special-case earthen clay variants that
        // carry their own water (waterlogged block / moist farmland).
        if ((below.is(ModBlocks.EARTHEN_CLAY_FARMLAND.get()) && below.getValue(EarthenClayFarmlandBlock.MOISTURE) > 0) ||
            (below.is(ModBlocks.EARTHEN_CLAY.get()) && below.getValue(BlockStateProperties.WATERLOGGED))) {
            cir.setReturnValue(true);
            return;
        }

        // Defensive fallback: any IW soil block, with water orthogonally
        // adjacent to it (mirrors vanilla's BlockTags.DIRT/SAND behavior).
        // Robust against modpack tag overrides that wipe minecraft:dirt.
        if (below.is(ModTags.IW_SOIL_PLACEABLE)) {
            BlockPos belowPos = pos.below();
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                if (level.getFluidState(belowPos.relative(direction)).is(FluidTags.WATER)) {
                    cir.setReturnValue(true);
                    return;
                }
            }
        }
    }
}
