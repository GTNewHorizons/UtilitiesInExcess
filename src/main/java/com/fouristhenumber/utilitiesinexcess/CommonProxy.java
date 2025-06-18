package com.fouristhenumber.utilitiesinexcess;

import com.fouristhenumber.utilitiesinexcess.utils.SoundVolumeChecks;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    public SoundVolumeChecks soundVolumeChecks;

    public void preInit(FMLPreInitializationEvent event) {
        // Config is handled in the early mixin loader (UIEMixinLoader)
        // since we want the config to be available
        // during mixin initialisation time.
        ModBlocks.init();
        ModItems.init();
    }

    public void init(FMLInitializationEvent event) {
        soundVolumeChecks = new SoundVolumeChecks();
    }

    public void postInit(FMLPostInitializationEvent event) {}

    public void serverStarting(FMLServerStartingEvent event) {}
}
