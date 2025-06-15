package com.fouristhenumber.utilitiesinexcess;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntitySoundMuffler;
import com.fouristhenumber.utilitiesinexcess.network.PacketSoundMuffler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fouristhenumber.utilitiesinexcess.common.recipe.RecipeLoader;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityDrum;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityRedstoneClock;
import com.fouristhenumber.utilitiesinexcess.utils.EventHandler;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(
    modid = UtilitiesInExcess.MODID,
    version = Tags.VERSION,
    name = "UtilitiesInExcess",
    acceptedMinecraftVersions = "[1.7.10]",
    dependencies = "required-after:gtnhlib@[0.5.14,)")
public class UtilitiesInExcess {

    public static final String MODID = "utilitiesinexcess";
    public static final Logger LOG = LogManager.getLogger(MODID);

    @SidedProxy(
        clientSide = "com.fouristhenumber.utilitiesinexcess.ClientProxy",
        serverSide = "com.fouristhenumber.utilitiesinexcess.CommonProxy")
    public static CommonProxy proxy;

    public static SimpleNetworkWrapper networkWrapper;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);

        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        networkWrapper.registerMessage(PacketSoundMuffler.class, PacketSoundMuffler.class, 1, Side.CLIENT);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);

        RecipeLoader.run();

        MinecraftForge.EVENT_BUS.register(new EventHandler());
        GameRegistry.registerTileEntity(TileEntityRedstoneClock.class, "TileEntityRedstoneClock");
        GameRegistry.registerTileEntity(TileEntityDrum.class, "TileEntityDrum");
        GameRegistry.registerTileEntity(TileEntitySoundMuffler.class, "TileEntitySoundMuffler");
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
