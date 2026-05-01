package com.ordana.immersive_weathering.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;

/**
 * STUB. The 1.20.1 build used Moonlight's old ChannelHandler/NetworkDir
 * pipeline; Moonlight 2.29.x replaced that with NetworkHelper +
 * CustomPacketPayload.TypeAndCodec, and SendCustomParticlesPacket would
 * need a full rewrite to register and (de)serialize on the new pipeline.
 *
 * For this port the channel is a no-op: callers call CHANNEL.sendToAllClientPlayersInRange(...),
 * which now silently drops the message. The visible regression is that
 * weathering events relying on server-pushed particles (e.g. leaf decay)
 * won't show client-side particles. Block transitions, sound effects, and
 * everything not particle-network-driven still work.
 *
 * Follow-up: register a real CustomPacketPayload.TypeAndCodec via
 * NetworkHelper.addNetworkRegistration(...) and forward the packet to
 * a client handler.
 */
public class NetworkHandler {

    public static final Channel CHANNEL = new Channel();

    public static void init() {
        // no-op stub; see class javadoc.
    }

    public static final class Channel {
        public void sendToAllClientPlayersInRange(ServerLevel level, BlockPos pos, double radius, CustomPacketPayload payload) {
            // no-op
        }
        public void sendToAllClientPlayersInRange(ServerLevel level, BlockPos pos, double radius, Object payload) {
            // no-op overload for legacy SendCustomParticlesPacket (which no longer
            // implements CustomPacketPayload).
        }
    }
}
