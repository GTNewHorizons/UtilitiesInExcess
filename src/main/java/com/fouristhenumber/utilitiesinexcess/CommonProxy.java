package com.fouristhenumber.utilitiesinexcess;

import net.minecraftforge.common.MinecraftForge;

import com.fouristhenumber.utilitiesinexcess.common.recipe.RecipeLoader;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityDrum;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityRedstoneClock;
import com.fouristhenumber.utilitiesinexcess.config.BlockConfig;
import com.fouristhenumber.utilitiesinexcess.config.ItemConfig;
import com.fouristhenumber.utilitiesinexcess.network.PacketHandler;
import com.fouristhenumber.utilitiesinexcess.utils.EventHandler;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        try {
            ConfigurationManager.registerConfig(ItemConfig.class);
            ConfigurationManager.registerConfig(BlockConfig.class);
        } catch (ConfigException e) {
            throw new RuntimeException(e);
        }
        PacketHandler.init();
        ModBlocks.init();
        ModItems.init();
    }

    public void init(FMLInitializationEvent event) {
        RecipeLoader.run();

        MinecraftForge.EVENT_BUS.register(new EventHandler());
        GameRegistry.registerTileEntity(TileEntityRedstoneClock.class, "TileEntityRedstoneClock");
        GameRegistry.registerTileEntity(TileEntityDrum.class, "TileEntityDrum");
    }

    public void postInit(FMLPostInitializationEvent event) {}

    public void serverStarting(FMLServerStartingEvent event) {}
}
