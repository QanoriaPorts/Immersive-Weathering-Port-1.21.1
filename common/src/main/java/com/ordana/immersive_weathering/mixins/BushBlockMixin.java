package com.ordana.immersive_weathering.mixins;

import com.ordana.immersive_weathering.reg.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BushBlock.class)
public abstract class BushBlockMixin extends Block {

    protected BushBlockMixin(Properties settings) {
        super(settings);
    }

    @Inject(method = "mayPlaceOn", at = @At(value = "HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    protected void mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        // Defensive fallback: also accept any IW soil block directly. This
        // survives third-party mods (e.g. Still Life) that ship a
        // "replace: true" override of vanilla minecraft:dirt and wipe the IW
        // additions out of BlockTags.DIRT.
        if (state.is(ModTags.FERTILE_BLOCKS) ||
            state.is(ModTags.IW_SOIL_PLACEABLE) ||
            (state.is(ModTags.CRACKED) && state.isFaceSturdy(level, pos, Direction.UP)))
            cir.setReturnValue(true);
    }
}
