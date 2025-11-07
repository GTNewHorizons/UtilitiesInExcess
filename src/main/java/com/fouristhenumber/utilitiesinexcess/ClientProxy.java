package com.fouristhenumber.utilitiesinexcess;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityCollector;
import com.fouristhenumber.utilitiesinexcess.render.CollectorLine;

import cpw.mods.fml.client.registry.ClientRegistry;

import net.minecraftforge.client.MinecraftForgeClient;

import com.fouristhenumber.utilitiesinexcess.common.renderers.InvertedIngotRenderer;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityPortalUnderWorld;
import com.fouristhenumber.utilitiesinexcess.render.ISBRHUnderworldPortal;
import com.fouristhenumber.utilitiesinexcess.render.TESRUnderworldPortal;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityCollector;
import com.fouristhenumber.utilitiesinexcess.render.CollectorLine;
import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        if (ModItems.INVERTED_INGOT.isEnabled()) {
            MinecraftForgeClient.registerItemRenderer(ModItems.INVERTED_INGOT.get(), new InvertedIngotRenderer());
        }
        if (ModBlocks.UNDERWORLD_PORTAL.isEnabled()) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPortalUnderWorld.class, new TESRUnderworldPortal());
            RenderingRegistry.registerBlockHandler(ISBRHUnderworldPortal.INSTANCE);
        }
        if(ModBlocks.COLLECTOR.isEnabled()){
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCollector.class, new CollectorLine());

        }
    }
}
