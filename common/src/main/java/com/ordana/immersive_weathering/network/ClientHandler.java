package com.ordana.immersive_weathering.network;

import com.ordana.immersive_weathering.configs.ClientConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Client-only handler for {@link SendCustomParticlesPacket}. Loaded lazily by
 * {@link SendCustomParticlesPacket#handle} only on the client physical side, so
 * dedicated servers never link {@link Minecraft}.
 */
final class ClientHandler {

    private ClientHandler() {}

    static void handle(SendCustomParticlesPacket packet) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        var level = player.level();

        BlockPos pos = packet.pos;
        if (packet.type == SendCustomParticlesPacket.EventType.DECAY_LEAVES) {
            if (ClientConfigs.LEAF_DECAY_PARTICLES.get()) {
                BlockState state = Block.stateById(packet.extraData);
                var leafParticle = new BlockParticleOption(ParticleTypes.BLOCK, state);
                int color = Minecraft.getInstance().getBlockColors().getColor(state, level, pos, 0);

                for (int i = 0; i < 20; i++) {
                    double d = pos.getX() + level.random.nextDouble();
                    double e = pos.getY() - 0.05;
                    double f = pos.getZ() + level.random.nextDouble();
                    level.addParticle(leafParticle, d, e, f, 0.0, color, 0.0);
                }
            }

            if (ClientConfigs.LEAF_DECAY_SOUND.get()) {
                level.playSound(player, pos, SoundEvents.AZALEA_LEAVES_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
        }
    }
}
