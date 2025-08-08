package com.fouristhenumber.utilitiesinexcess.network;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.network.client.ParticlePacket;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(UtilitiesInExcess.MODID);

    public static void init() {
        int packetId = 0;
        INSTANCE.registerMessage(ParticlePacket.Handler.class, ParticlePacket.class, packetId++, Side.CLIENT);
    }
}
