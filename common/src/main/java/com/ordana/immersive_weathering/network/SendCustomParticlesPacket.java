package com.ordana.immersive_weathering.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

/**
 * STUB. The 1.20.1 build implemented Moonlight's old {@code Message}
 * interface (with {@code writeToBuffer}/{@code handle(ChannelHandler.Context)}).
 * Moonlight 2.29.x replaced that with {@code CustomPacketPayload.TypeAndCodec}
 * registered via {@link NetworkHandler}. Until that registration is rewritten,
 * this class keeps the constructors used by call sites (so the codebase still
 * compiles) but does not actually serialize or get sent over the wire — see
 * {@link NetworkHandler} javadoc.
 */
public class SendCustomParticlesPacket {

    public final EventType type;
    public final int extraData;
    public final BlockPos pos;

    public SendCustomParticlesPacket(FriendlyByteBuf buffer) {
        this.extraData = buffer.readInt();
        this.type = EventType.values()[buffer.readByte()];
        this.pos = buffer.readBlockPos();
    }

    public SendCustomParticlesPacket(EventType type, BlockPos pos, int extraData) {
        this.type = type;
        this.pos = pos;
        this.extraData = extraData;
    }

    public enum EventType {
        DECAY_LEAVES
    }
}
