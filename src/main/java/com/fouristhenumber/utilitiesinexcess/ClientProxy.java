package com.fouristhenumber.utilitiesinexcess;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityPortalUnderWorld;
import com.fouristhenumber.utilitiesinexcess.render.ISBRHUnderworldPortal;
import com.fouristhenumber.utilitiesinexcess.render.TESRUnderworldPortal;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        if (ModBlocks.UNDERWORLD_PORTAL.isEnabled()) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPortalUnderWorld.class, new TESRUnderworldPortal());
            RenderingRegistry.registerBlockHandler(ISBRHUnderworldPortal.INSTANCE);
        }
    }
}
