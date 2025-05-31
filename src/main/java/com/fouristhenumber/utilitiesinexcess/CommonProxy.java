package com.fouristhenumber.utilitiesinexcess;

import com.fouristhenumber.utilitiesinexcess.config.BlockConfig;
import com.fouristhenumber.utilitiesinexcess.config.ItemConfig;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        try {
            ConfigurationManager.registerConfig(ItemConfig.class);
            ConfigurationManager.registerConfig(BlockConfig.class);
        } catch (ConfigException e) {
            throw new RuntimeException(e);
        }

        ModBlocks.init();
        ModItems.init();
    }

    public void init(FMLInitializationEvent event) {}

    public void postInit(FMLPostInitializationEvent event) {}

    public void serverStarting(FMLServerStartingEvent event) {}
}
