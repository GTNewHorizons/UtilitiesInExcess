package com.fouristhenumber.utilitiesinexcess;

import com.fouristhenumber.utilitiesinexcess.common.renderers.SpikeTileRenderer;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntitySpike;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySpike.class, new SpikeTileRenderer());
        super.preInit(event);
    }
}
