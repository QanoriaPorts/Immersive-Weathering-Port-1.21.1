package com.ordana.immersive_weathering.blocks.frosted;

import com.mojang.serialization.MapCodec;
import dev.architectury.injectables.annotations.PlatformOnly;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

public class FrostyGlassBlock extends HalfTransparentBlock implements Frosty {

    public static final MapCodec<FrostyGlassBlock> CODEC = simpleCodec(FrostyGlassBlock::new);

    @Override
    protected MapCodec<? extends HalfTransparentBlock> codec() {
        return CODEC;
    }

    public FrostyGlassBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(NATURAL, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(NATURAL);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        tryUnFrost(state, level, pos);
    }

    @Override
    public boolean skipRendering(BlockState blockState, BlockState neighborState, Direction direction) {
        if (neighborState.is(Blocks.GLASS)) return true;
        return super.skipRendering(blockState, neighborState, direction);
    }

    @PlatformOnly(PlatformOnly.FORGE)
    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
        if (neighborState.is(this) || neighborState.is(Blocks.GLASS)) return true;
        return false;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        InteractionResult success = interactWithPlayer(state, level, pos, player, player.getUsedItemHand());
        if (success != InteractionResult.PASS) return success;

        return super.useWithoutItem(state, level, pos, player, hit);
    }

}
