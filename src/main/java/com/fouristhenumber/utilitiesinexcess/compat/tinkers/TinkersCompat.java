package com.fouristhenumber.utilitiesinexcess.compat.tinkers;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import tconstruct.library.TConstructRegistry;

public class TinkersCompat {

    public static void init() {
        TinkersEvents tinkersEvents = new TinkersEvents();
        MinecraftForge.EVENT_BUS.register(tinkersEvents);
        FMLCommonHandler.instance()
            .bus()
            .register(tinkersEvents);

        TConstructRegistry.registerActiveToolMod(new BedrockiumActiveToolMod());
        TinkersMaterials.registerMaterials();
    }
}
