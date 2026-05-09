package com.ordana.immersive_weathering.network;

import net.mehvahdjukaar.moonlight.api.platform.network.NetworkHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;

/**
 * Wires the IW custom-particle packet through Moonlight's NetworkHelper, which
 * abstracts NeoForge 1.21's CustomPacketPayload registration. The 1.20.1 build
 * used Moonlight's older ChannelHandler API directly; that API was removed in
 * Moonlight 2.29.x and replaced with NetworkHelper + CustomPacketPayload.TypeAndCodec.
 */
public class NetworkHandler {

    public static final Channel CHANNEL = new Channel();

    public static void init() {
        NetworkHelper.addNetworkRegistration(event -> {
            event.registerClientBound(SendCustomParticlesPacket.TYPE);
        }, 0);
    }

    public static final class Channel {
        public void sendToAllClientPlayersInRange(ServerLevel level, BlockPos pos, double radius, CustomPacketPayload payload) {
            NetworkHelper.sendToAllClientPlayersInRange(level, pos, radius, payload);
        }
    }
}
