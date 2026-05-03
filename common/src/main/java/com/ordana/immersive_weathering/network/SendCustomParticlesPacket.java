package com.ordana.immersive_weathering.network;

import com.ordana.immersive_weathering.ImmersiveWeathering;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class SendCustomParticlesPacket implements Message {

    public static final CustomPacketPayload.TypeAndCodec<RegistryFriendlyByteBuf, SendCustomParticlesPacket> TYPE =
            Message.makeType(ImmersiveWeathering.res("custom_particles"), SendCustomParticlesPacket::new);

    public final EventType type;
    public final int extraData;
    public final BlockPos pos;

    public SendCustomParticlesPacket(RegistryFriendlyByteBuf buffer) {
        this.extraData = buffer.readInt();
        this.type = EventType.values()[buffer.readByte()];
        this.pos = buffer.readBlockPos();
    }

    public SendCustomParticlesPacket(EventType type, BlockPos pos, int extraData) {
        this.type = type;
        this.pos = pos;
        this.extraData = extraData;
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.extraData);
        buf.writeByte(this.type.ordinal());
        buf.writeBlockPos(this.pos);
    }

    @Override
    public void handle(Message.Context context) {
        if (PlatHelper.getPhysicalSide().isClient()) {
            ClientHandler.handle(this);
        }
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE.type();
    }

    public enum EventType {
        DECAY_LEAVES
    }
}
