package com.fouristhenumber.utilitiesinexcess;

import net.minecraftforge.client.MinecraftForgeClient;

import com.fouristhenumber.utilitiesinexcess.common.renderers.GloveRenderer;
import com.fouristhenumber.utilitiesinexcess.common.renderers.InvertedIngotRenderer;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityPortalUnderWorld;
import com.fouristhenumber.utilitiesinexcess.render.ISBRHUnderworldPortal;
import com.fouristhenumber.utilitiesinexcess.render.TESRUnderworldPortal;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        if (ModItems.INVERTED_NUGGET.isEnabled()) {
            MinecraftForgeClient.registerItemRenderer(ModItems.INVERTED_INGOT.get(), new InvertedIngotRenderer());
        }
        if (ModBlocks.UNDERWORLD_PORTAL.isEnabled()) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPortalUnderWorld.class, new TESRUnderworldPortal());
            RenderingRegistry.registerBlockHandler(ISBRHUnderworldPortal.INSTANCE);
        }
        if (ModItems.GLOVE.isEnabled()) {
            MinecraftForgeClient.registerItemRenderer(ModItems.GLOVE.get(), new GloveRenderer());
        }
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }
}
