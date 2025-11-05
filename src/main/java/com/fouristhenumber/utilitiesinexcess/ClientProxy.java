package com.fouristhenumber.utilitiesinexcess;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityCollector;
import com.fouristhenumber.utilitiesinexcess.render.CollectorLine;
import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {



    @Override
    public void registerRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCollector.class, new CollectorLine());
    }
}
