package com.fouristhenumber.utilitiesinexcess;

import com.gtnewhorizon.gtnhlib.client.model.loading.ModelRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.client.MinecraftForgeClient;

import com.fouristhenumber.utilitiesinexcess.common.renderers.InvertedIngotRenderer;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityPortalUnderWorld;
import com.fouristhenumber.utilitiesinexcess.render.ISBRHUnderworldPortal;
import com.fouristhenumber.utilitiesinexcess.render.TESRUnderworldPortal;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;

import static com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess.MODID;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        ModelRegistry.registerModid(MODID);
    }

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
    }
}
