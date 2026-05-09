package com.ordana.immersive_weathering.mixins;

import com.ordana.immersive_weathering.blocks.ModPropaguleBlock;
import com.ordana.immersive_weathering.blocks.cracked.*;
import com.ordana.immersive_weathering.blocks.mossy.*;
import com.ordana.immersive_weathering.blocks.rusty.Rustable;
import com.ordana.immersive_weathering.blocks.rusty.RustableBarsBlock;
import com.ordana.immersive_weathering.blocks.rusty.RustableDoorBlock;
import com.ordana.immersive_weathering.blocks.rusty.RustableTrapdoorBlock;
import com.ordana.immersive_weathering.reg.ModItems;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

/**
 * Replaces select vanilla block constructors during {@code Blocks.<clinit>}
 * with weathering-aware subclasses, so that vanilla cobblestone/stone bricks
 * become mossy or crack, and vanilla iron bars/doors/trapdoors rust.
 *
 * <p>1.21.1 differences from the original 1.20.1 layout:
 * <ul>
 *   <li>{@code DoorBlock}/{@code TrapDoorBlock} args are now {@code (BlockSetType, Properties)}
 *       instead of {@code (Properties, BlockSetType)}.</li>
 *   <li>{@code MangrovePropaguleBlock} now takes {@code (TreeGrower, Properties)}.</li>
 *   <li>Stairs are no longer {@code new StairBlock(BlockState, Properties)} inline;
 *       vanilla calls {@code Blocks.legacyStair(Block) -> Block} instead, so we
 *       redirect that {@code INVOKE} site for stair entries.</li>
 * </ul>
 */
@Mixin(Blocks.class)
public abstract class BlocksMixin {

    @Shadow @Final public static Block PRISMARINE_BRICKS;
    @Shadow @Final public static Block END_STONE_BRICKS;

    // ===================== mangrove propagule =====================

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW",
                    target = "(Lnet/minecraft/world/level/block/grower/TreeGrower;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/MangrovePropaguleBlock;",
                    ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=mangrove_propagule")))
    private static MangrovePropaguleBlock iw$mangrovePropagule(TreeGrower grower, BlockBehaviour.Properties props) {
        return new ModPropaguleBlock(props);
    }

    // ===================== bricks (mossable + crackable) =====================

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=bricks")))
    private static Block iw$bricks(BlockBehaviour.Properties p) {
        return new CrackableMossableBlock(Mossable.MossLevel.UNAFFECTED, Crackable.CrackLevel.UNCRACKED, () -> Items.BRICK, p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/SlabBlock;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=brick_slab")))
    private static SlabBlock iw$brickSlab(BlockBehaviour.Properties p) {
        return new CrackableMossableSlabBlock(Mossable.MossLevel.UNAFFECTED, Crackable.CrackLevel.UNCRACKED, () -> Items.BRICK, p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;legacyStair(Lnet/minecraft/world/level/block/Block;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=brick_stairs")))
    private static Block iw$brickStairs(Block base) {
        return new CrackableMossableStairsBlock(Mossable.MossLevel.UNAFFECTED, Crackable.CrackLevel.UNCRACKED, () -> Items.BRICK, () -> Blocks.BRICKS, BlockBehaviour.Properties.ofFullCopy(base));
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/WallBlock;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=brick_wall")))
    private static WallBlock iw$brickWall(BlockBehaviour.Properties p) {
        return new CrackableMossableWallBlock(Mossable.MossLevel.UNAFFECTED, Crackable.CrackLevel.UNCRACKED, () -> Items.BRICK, p);
    }

    // ===================== stone =====================

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=stone")))
    private static Block iw$stone(BlockBehaviour.Properties p) {
        return new MossableBlock(Mossable.MossLevel.UNAFFECTED, p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/SlabBlock;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=stone_slab")))
    private static SlabBlock iw$stoneSlab(BlockBehaviour.Properties p) {
        return new MossableSlabBlock(Mossable.MossLevel.UNAFFECTED, p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;legacyStair(Lnet/minecraft/world/level/block/Block;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=stone_stairs")))
    private static Block iw$stoneStairs(Block base) {
        return new MossableStairsBlock(Mossable.MossLevel.UNAFFECTED, () -> Blocks.STONE, BlockBehaviour.Properties.ofFullCopy(base));
    }

    // ===================== cobblestone =====================

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=cobblestone")))
    private static Block iw$cobblestone(BlockBehaviour.Properties p) {
        return new MossableBlock(Mossable.MossLevel.UNAFFECTED, p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/SlabBlock;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=cobblestone_slab")))
    private static SlabBlock iw$cobblestoneSlab(BlockBehaviour.Properties p) {
        return new MossableSlabBlock(Mossable.MossLevel.UNAFFECTED, p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;legacyStair(Lnet/minecraft/world/level/block/Block;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=cobblestone_stairs")))
    private static Block iw$cobblestoneStairs(Block base) {
        return new MossableStairsBlock(Mossable.MossLevel.UNAFFECTED, () -> Blocks.COBBLESTONE, BlockBehaviour.Properties.ofFullCopy(base));
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/WallBlock;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=cobblestone_wall")))
    private static WallBlock iw$cobblestoneWall(BlockBehaviour.Properties p) {
        return new MossableWallBlock(Mossable.MossLevel.UNAFFECTED, p);
    }

    // ===================== stone bricks =====================

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=stone_bricks")))
    private static Block iw$stoneBricks(BlockBehaviour.Properties p) {
        return new CrackableMossableBlock(Mossable.MossLevel.UNAFFECTED, Crackable.CrackLevel.UNCRACKED, () -> ModItems.STONE_BRICK.get(), p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/SlabBlock;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=stone_brick_slab")))
    private static SlabBlock iw$stoneBrickSlab(BlockBehaviour.Properties p) {
        return new CrackableMossableSlabBlock(Mossable.MossLevel.UNAFFECTED, Crackable.CrackLevel.UNCRACKED, () -> ModItems.STONE_BRICK.get(), p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;legacyStair(Lnet/minecraft/world/level/block/Block;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=stone_brick_stairs")))
    private static Block iw$stoneBrickStairs(Block base) {
        return new CrackableMossableStairsBlock(Mossable.MossLevel.UNAFFECTED, Crackable.CrackLevel.UNCRACKED, () -> ModItems.STONE_BRICK.get(), () -> Blocks.STONE_BRICKS, BlockBehaviour.Properties.ofFullCopy(base));
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/WallBlock;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=stone_brick_wall")))
    private static WallBlock iw$stoneBrickWall(BlockBehaviour.Properties p) {
        return new CrackableMossableWallBlock(Mossable.MossLevel.UNAFFECTED, Crackable.CrackLevel.UNCRACKED, () -> ModItems.STONE_BRICK.get(), p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=chiseled_stone_bricks")))
    private static Block iw$chiseledStoneBricks(BlockBehaviour.Properties p) {
        return new MossableBlock(Mossable.MossLevel.UNAFFECTED, p);
    }

    // ===================== mossy_stone_bricks (already mossy) =====================

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=mossy_stone_bricks")))
    private static Block iw$mossyStoneBricks(BlockBehaviour.Properties p) {
        return new MossyBlock(Mossable.MossLevel.MOSSY, p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/SlabBlock;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=mossy_stone_brick_slab")))
    private static SlabBlock iw$mossyStoneBrickSlab(BlockBehaviour.Properties p) {
        return new MossySlabBlock(Mossable.MossLevel.MOSSY, p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;legacyStair(Lnet/minecraft/world/level/block/Block;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=mossy_stone_brick_stairs")))
    private static Block iw$mossyStoneBrickStairs(Block base) {
        return new MossyStairsBlock(Mossable.MossLevel.MOSSY, () -> Blocks.MOSSY_STONE_BRICKS, BlockBehaviour.Properties.ofFullCopy(base));
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/WallBlock;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=mossy_stone_brick_wall")))
    private static WallBlock iw$mossyStoneBrickWall(BlockBehaviour.Properties p) {
        return new MossyWallBlock(Mossable.MossLevel.MOSSY, p);
    }

    // ===================== mossy_cobblestone (already mossy) =====================

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=mossy_cobblestone")))
    private static Block iw$mossyCobblestone(BlockBehaviour.Properties p) {
        return new MossyBlock(Mossable.MossLevel.MOSSY, p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/SlabBlock;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=mossy_cobblestone_slab")))
    private static SlabBlock iw$mossyCobblestoneSlab(BlockBehaviour.Properties p) {
        return new MossySlabBlock(Mossable.MossLevel.MOSSY, p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;legacyStair(Lnet/minecraft/world/level/block/Block;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=mossy_cobblestone_stairs")))
    private static Block iw$mossyCobblestoneStairs(Block base) {
        return new MossyStairsBlock(Mossable.MossLevel.MOSSY, () -> Blocks.MOSSY_COBBLESTONE, BlockBehaviour.Properties.ofFullCopy(base));
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/WallBlock;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=mossy_cobblestone_wall")))
    private static WallBlock iw$mossyCobblestoneWall(BlockBehaviour.Properties p) {
        return new MossyWallBlock(Mossable.MossLevel.MOSSY, p);
    }

    // ===================== polished blackstone (crackable) =====================

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=polished_blackstone_bricks")))
    private static Block iw$polishedBlackstoneBricks(BlockBehaviour.Properties p) {
        return new CrackableBlock(Crackable.CrackLevel.UNCRACKED, () -> ModItems.BLACKSTONE_BRICK.get(), p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/SlabBlock;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=polished_blackstone_brick_slab")))
    private static SlabBlock iw$polishedBlackstoneBrickSlab(BlockBehaviour.Properties p) {
        return new CrackableSlabBlock(Crackable.CrackLevel.UNCRACKED, () -> ModItems.BLACKSTONE_BRICK.get(), p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;legacyStair(Lnet/minecraft/world/level/block/Block;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=polished_blackstone_brick_stairs")))
    private static Block iw$polishedBlackstoneBrickStairs(Block base) {
        return new CrackableStairsBlock(Crackable.CrackLevel.UNCRACKED, () -> Blocks.POLISHED_BLACKSTONE_BRICKS, () -> ModItems.BLACKSTONE_BRICK.get(), BlockBehaviour.Properties.ofFullCopy(base));
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/WallBlock;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=polished_blackstone_brick_wall")))
    private static WallBlock iw$polishedBlackstoneBrickWall(BlockBehaviour.Properties p) {
        return new CrackableWallBlock(Crackable.CrackLevel.UNCRACKED, () -> ModItems.BLACKSTONE_BRICK.get(), p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=cracked_polished_blackstone_bricks")))
    private static Block iw$crackedPolishedBlackstoneBricks(BlockBehaviour.Properties p) {
        return new CrackedBlock(Crackable.CrackLevel.CRACKED, () -> ModItems.BLACKSTONE_BRICK.get(), p);
    }

    // ===================== cracked variants (already cracked) =====================

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=cracked_stone_bricks")))
    private static Block iw$crackedStoneBricks(BlockBehaviour.Properties p) {
        return new CrackedBlock(Crackable.CrackLevel.CRACKED, () -> ModItems.STONE_BRICK.get(), p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=cracked_deepslate_bricks")))
    private static Block iw$crackedDeepslateBricks(BlockBehaviour.Properties p) {
        return new CrackedBlock(Crackable.CrackLevel.CRACKED, () -> ModItems.DEEPSLATE_BRICK.get(), p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=cracked_deepslate_tiles")))
    private static Block iw$crackedDeepslateTiles(BlockBehaviour.Properties p) {
        return new CrackedBlock(Crackable.CrackLevel.CRACKED, () -> ModItems.DEEPSLATE_TILE.get(), p);
    }

    // ===================== deepslate bricks =====================

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=deepslate_bricks")))
    private static Block iw$deepslateBricks(BlockBehaviour.Properties p) {
        return new CrackableBlock(Crackable.CrackLevel.UNCRACKED, () -> ModItems.DEEPSLATE_BRICK.get(), p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/SlabBlock;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=deepslate_brick_slab")))
    private static SlabBlock iw$deepslateBrickSlab(BlockBehaviour.Properties p) {
        return new CrackableSlabBlock(Crackable.CrackLevel.UNCRACKED, () -> ModItems.DEEPSLATE_BRICK.get(), p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;legacyStair(Lnet/minecraft/world/level/block/Block;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=deepslate_brick_stairs")))
    private static Block iw$deepslateBrickStairs(Block base) {
        return new CrackableStairsBlock(Crackable.CrackLevel.UNCRACKED, () -> Blocks.DEEPSLATE_BRICKS, () -> ModItems.DEEPSLATE_BRICK.get(), BlockBehaviour.Properties.ofFullCopy(base));
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/WallBlock;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=deepslate_brick_wall")))
    private static WallBlock iw$deepslateBrickWall(BlockBehaviour.Properties p) {
        return new CrackableWallBlock(Crackable.CrackLevel.UNCRACKED, () -> ModItems.DEEPSLATE_BRICK.get(), p);
    }

    // ===================== deepslate tiles =====================

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=deepslate_tiles")))
    private static Block iw$deepslateTiles(BlockBehaviour.Properties p) {
        return new CrackableBlock(Crackable.CrackLevel.UNCRACKED, () -> ModItems.DEEPSLATE_TILE.get(), p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/SlabBlock;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=deepslate_tile_slab")))
    private static SlabBlock iw$deepslateTileSlab(BlockBehaviour.Properties p) {
        return new CrackableSlabBlock(Crackable.CrackLevel.UNCRACKED, () -> ModItems.DEEPSLATE_TILE.get(), p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;legacyStair(Lnet/minecraft/world/level/block/Block;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=deepslate_tile_stairs")))
    private static Block iw$deepslateTileStairs(Block base) {
        return new CrackableStairsBlock(Crackable.CrackLevel.UNCRACKED, () -> Blocks.DEEPSLATE_TILES, () -> ModItems.DEEPSLATE_TILE.get(), BlockBehaviour.Properties.ofFullCopy(base));
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/WallBlock;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=deepslate_tile_wall")))
    private static WallBlock iw$deepslateTileWall(BlockBehaviour.Properties p) {
        return new CrackableWallBlock(Crackable.CrackLevel.UNCRACKED, () -> ModItems.DEEPSLATE_TILE.get(), p);
    }

    // ===================== nether bricks =====================

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=nether_bricks")))
    private static Block iw$netherBricks(BlockBehaviour.Properties p) {
        return new CrackableBlock(Crackable.CrackLevel.UNCRACKED, () -> Items.NETHER_BRICK, p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/SlabBlock;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=nether_brick_slab")))
    private static SlabBlock iw$netherBrickSlab(BlockBehaviour.Properties p) {
        return new CrackableSlabBlock(Crackable.CrackLevel.UNCRACKED, () -> Items.NETHER_BRICK, p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;legacyStair(Lnet/minecraft/world/level/block/Block;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=nether_brick_stairs")))
    private static Block iw$netherBrickStairs(Block base) {
        return new CrackableStairsBlock(Crackable.CrackLevel.UNCRACKED, () -> Blocks.NETHER_BRICKS, () -> Items.NETHER_BRICK, BlockBehaviour.Properties.ofFullCopy(base));
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/WallBlock;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=nether_brick_wall")))
    private static WallBlock iw$netherBrickWall(BlockBehaviour.Properties p) {
        return new CrackableWallBlock(Crackable.CrackLevel.UNCRACKED, () -> Items.NETHER_BRICK, p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=cracked_nether_bricks")))
    private static Block iw$crackedNetherBricks(BlockBehaviour.Properties p) {
        return new CrackedBlock(Crackable.CrackLevel.CRACKED, () -> Items.NETHER_BRICK, p);
    }

    // ===================== prismarine bricks =====================

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=prismarine_bricks")))
    private static Block iw$prismarineBricks(BlockBehaviour.Properties p) {
        return new CrackableBlock(Crackable.CrackLevel.UNCRACKED, () -> ModItems.PRISMARINE_BRICK.get(), p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/SlabBlock;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=prismarine_brick_slab")))
    private static SlabBlock iw$prismarineBrickSlab(BlockBehaviour.Properties p) {
        return new CrackableSlabBlock(Crackable.CrackLevel.UNCRACKED, () -> ModItems.PRISMARINE_BRICK.get(), p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;legacyStair(Lnet/minecraft/world/level/block/Block;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=prismarine_brick_stairs")))
    private static Block iw$prismarineBrickStairs(Block base) {
        return new CrackableStairsBlock(Crackable.CrackLevel.UNCRACKED, () -> PRISMARINE_BRICKS, () -> ModItems.PRISMARINE_BRICK.get(), BlockBehaviour.Properties.ofFullCopy(base));
    }

    // ===================== end stone bricks =====================

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=end_stone_bricks")))
    private static Block iw$endStoneBricks(BlockBehaviour.Properties p) {
        return new CrackableBlock(Crackable.CrackLevel.UNCRACKED, () -> ModItems.END_STONE_BRICK.get(), p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/SlabBlock;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=end_stone_brick_slab")))
    private static SlabBlock iw$endStoneBrickSlab(BlockBehaviour.Properties p) {
        return new CrackableSlabBlock(Crackable.CrackLevel.UNCRACKED, () -> ModItems.END_STONE_BRICK.get(), p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;legacyStair(Lnet/minecraft/world/level/block/Block;)Lnet/minecraft/world/level/block/Block;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=end_stone_brick_stairs")))
    private static Block iw$endStoneBrickStairs(Block base) {
        return new CrackableStairsBlock(Crackable.CrackLevel.UNCRACKED, () -> END_STONE_BRICKS, () -> ModItems.END_STONE_BRICK.get(), BlockBehaviour.Properties.ofFullCopy(base));
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/WallBlock;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=end_stone_brick_wall")))
    private static WallBlock iw$endStoneBrickWall(BlockBehaviour.Properties p) {
        return new CrackableWallBlock(Crackable.CrackLevel.UNCRACKED, () -> ModItems.END_STONE_BRICK.get(), p);
    }

    // ===================== iron (rustable) =====================

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/IronBarsBlock;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=iron_bars")))
    private static IronBarsBlock iw$ironBars(BlockBehaviour.Properties p) {
        return new RustableBarsBlock(Rustable.RustLevel.UNAFFECTED, p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/properties/BlockSetType;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/DoorBlock;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=iron_door")))
    private static DoorBlock iw$ironDoor(BlockSetType type, BlockBehaviour.Properties p) {
        return new RustableDoorBlock(Rustable.RustLevel.UNAFFECTED, p);
    }

    @Redirect(method = "<clinit>",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/properties/BlockSetType;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/TrapDoorBlock;", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=iron_trapdoor")))
    private static TrapDoorBlock iw$ironTrapdoor(BlockSetType type, BlockBehaviour.Properties p) {
        return new RustableTrapdoorBlock(Rustable.RustLevel.UNAFFECTED, p);
    }
}
