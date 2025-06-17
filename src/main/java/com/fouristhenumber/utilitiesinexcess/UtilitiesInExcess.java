package com.fouristhenumber.utilitiesinexcess;

import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fouristhenumber.utilitiesinexcess.common.recipe.RecipeLoader;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityDrum;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityRainMuffler;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityRedstoneClock;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntitySoundMuffler;
import com.fouristhenumber.utilitiesinexcess.network.PacketRainMuffledSync;
import com.fouristhenumber.utilitiesinexcess.utils.EventHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(
    modid = UtilitiesInExcess.MODID,
    version = Tags.VERSION,
    name = "UtilitiesInExcess",
    acceptedMinecraftVersions = "[1.7.10]",
    dependencies = "required-after:gtnhlib@[0.6.31,)")
public class UtilitiesInExcess {

    public static final String MODID = "utilitiesinexcess";
    public static final Logger LOG = LogManager.getLogger(MODID);

    public static SimpleNetworkWrapper networkWrapper;

    @SidedProxy(
        clientSide = "com.fouristhenumber.utilitiesinexcess.ClientProxy",
        serverSide = "com.fouristhenumber.utilitiesinexcess.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);

        networkWrapper = new SimpleNetworkWrapper(MODID);
        networkWrapper.registerMessage(PacketRainMuffledSync.class, PacketRainMuffledSync.class, 1, Side.CLIENT);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);

        RecipeLoader.run();

        EventHandler handler = new EventHandler();
        MinecraftForge.EVENT_BUS.register(handler);
        FMLCommonHandler.instance()
            .bus()
            .register(handler);

        GameRegistry.registerTileEntity(TileEntityRedstoneClock.class, "TileEntityRedstoneClock");
        GameRegistry.registerTileEntity(TileEntityDrum.class, "TileEntityDrum");
        GameRegistry.registerTileEntity(TileEntitySoundMuffler.class, "TileEntitySoundMuffler");
        GameRegistry.registerTileEntity(TileEntityRainMuffler.class, "TileEntityRainMuffler");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }
}
