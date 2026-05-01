package com.ordana.immersive_weathering.neoforge;

import com.google.common.collect.ImmutableBiMap;
import com.ordana.immersive_weathering.integrations.IntegrationHandler;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.material.FlowingFluid;

public class IWPlatformStuffImpl {

    public static void addExtraMossyBlocks(ImmutableBiMap.Builder<Block, Block> builder) {
        if (IntegrationHandler.quark) {
           // QuarkPlugin.addAllVerticalSlabs(builder);
        }
    }

    public static void addExtraCrackedBlocks(ImmutableBiMap.Builder<Block, Block> builder) {
        if (IntegrationHandler.quark) {
            //QuarkPlugin.addAllVerticalSlabs(builder);
        }
    }

    public static FlowingFluid getFlowingFluid(LiquidBlock block) {
        // LiquidBlock.getFluid() was removed in 1.21.1; the fluid is a
        // protected field with no public accessor. Pull it from the default
        // block state instead — for vanilla water/lava liquid blocks the
        // FluidState always reports the underlying FlowingFluid.
        return (FlowingFluid) block.defaultBlockState().getFluidState().getType();
    }


    public static void addFeatureToBiome(GenerationStep.Decoration step, TagKey<Biome> tagKey, ResourceKey<PlacedFeature> feature) {
    }


}
