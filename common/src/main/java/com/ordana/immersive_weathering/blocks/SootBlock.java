package com.ordana.immersive_weathering.blocks;

import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MultifaceSpreader;

public class SootBlock extends MultifaceBlock {
    public static final com.mojang.serialization.MapCodec<SootBlock> CODEC = simpleCodec(SootBlock::new);

    @Override
    protected com.mojang.serialization.MapCodec<? extends MultifaceBlock> codec() {
        return CODEC;
    }

    private final MultifaceSpreader spreader = new MultifaceSpreader(this);

    public SootBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState());
    }

    @Override
    public MultifaceSpreader getSpreader() {
        return this.spreader;
    }
}
