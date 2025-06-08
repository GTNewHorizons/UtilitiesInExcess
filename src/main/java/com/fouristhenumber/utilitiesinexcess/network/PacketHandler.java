package com.fouristhenumber.utilitiesinexcess.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("UIE");

    public static void init() {
        int packetId = 0;
        INSTANCE.registerMessage(ParticlePacket.Handler.class, ParticlePacket.class, packetId++, Side.CLIENT);
    }
}
