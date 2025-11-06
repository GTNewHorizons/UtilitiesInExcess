package com.fouristhenumber.utilitiesinexcess;

import net.minecraftforge.client.MinecraftForgeClient;

import com.fouristhenumber.utilitiesinexcess.common.renderers.InvertedIngotRenderer;

import cpw.mods.fml.common.event.FMLInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        MinecraftForgeClient.registerItemRenderer(ModItems.INVERTED_INGOT.get(), new InvertedIngotRenderer());
        super.init(event);
    }
}
