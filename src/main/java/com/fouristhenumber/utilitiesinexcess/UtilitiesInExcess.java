package com.fouristhenumber.utilitiesinexcess;

import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fouristhenumber.utilitiesinexcess.common.recipe.RecipeLoader;
import com.fouristhenumber.utilitiesinexcess.common.renderers.LapisAetheriusRenderer;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityDrum;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityMarginallyMaximisedChest;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityPureLove;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityRainMuffler;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityRedstoneClock;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntitySignificantlyShrunkChest;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntitySoundMuffler;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityTrashCanEnergy;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityTrashCanFluid;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityTrashCanItem;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityEnderGenerator;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityFoodGenerator;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityFurnaceGenerator;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityHighTemperatureFurnaceGenerator;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityLavaGenerator;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityLowTemperatureFurnaceGenerator;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityNetherStarGenerator;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityPinkGenerator;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityPotionGenerator;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityRedstoneGenerator;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntitySolarGenerator;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityTNTGenerator;
import com.fouristhenumber.utilitiesinexcess.utils.EventHandler;
import com.fouristhenumber.utilitiesinexcess.utils.PinkFuelHelper;

import cpw.mods.fml.client.registry.RenderingRegistry;
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
    dependencies = "required-after:gtnhlib@[0.6.31,)")
public class UtilitiesInExcess {

    public static final String MODID = "utilitiesinexcess";
    public static final Logger LOG = LogManager.getLogger(MODID);

    public static int lapisAetheriusRenderID;

    @SidedProxy(
        clientSide = "com.fouristhenumber.utilitiesinexcess.ClientProxy",
        serverSide = "com.fouristhenumber.utilitiesinexcess.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);

        RecipeLoader.run();

        MinecraftForge.EVENT_BUS.register(new EventHandler());

        GameRegistry.registerTileEntity(TileEntityRedstoneClock.class, "TileEntityRedstoneClock");
        GameRegistry.registerTileEntity(TileEntityTrashCanItem.class, "TileEntityTrashCanItem");
        GameRegistry.registerTileEntity(TileEntityTrashCanFluid.class, "TileEntityTrashCanFluid");
        GameRegistry.registerTileEntity(TileEntityTrashCanEnergy.class, "TileEntityTrashCanEnergyUIE");
        GameRegistry.registerTileEntity(TileEntityDrum.class, "TileEntityDrum");
        GameRegistry.registerTileEntity(TileEntityPureLove.class, "TileEntityPureLove");
        GameRegistry.registerTileEntity(TileEntityMarginallyMaximisedChest.class, "TileEntityMarginallyMaximisedChest");
        GameRegistry.registerTileEntity(TileEntitySignificantlyShrunkChest.class, "TileEntitySignificantlyShrunkChest");
        GameRegistry.registerTileEntity(TileEntitySoundMuffler.class, "TileEntitySoundMufflerUIE");
        GameRegistry.registerTileEntity(TileEntityRainMuffler.class, "TileEntityRainMufflerUIE");

        GameRegistry.registerTileEntity(
            TileEntityLowTemperatureFurnaceGenerator.class,
            "TileEntityLowTemperatureFurnaceGeneratorUIE");
        GameRegistry.registerTileEntity(TileEntityFurnaceGenerator.class, "TileEntityFurnaceGeneratorUIE");
        GameRegistry.registerTileEntity(
            TileEntityHighTemperatureFurnaceGenerator.class,
            "TileEntityHighTemperatureFurnaceGeneratorUIE");
        GameRegistry.registerTileEntity(TileEntityLavaGenerator.class, "TileEntityLavaGeneratorUIE");
        GameRegistry.registerTileEntity(TileEntityEnderGenerator.class, "TileEntityEnderGeneratorUIE");
        GameRegistry.registerTileEntity(TileEntityRedstoneGenerator.class, "TileEntityRedstoneGeneratorUIE");
        GameRegistry.registerTileEntity(TileEntityFoodGenerator.class, "TileEntityFoodGeneratorUIE");
        GameRegistry.registerTileEntity(TileEntityPotionGenerator.class, "TileEntityPotionGeneratorUIE");
        GameRegistry.registerTileEntity(TileEntitySolarGenerator.class, "TileEntitySolarGeneratorUIE");
        GameRegistry.registerTileEntity(TileEntityTNTGenerator.class, "TileEntityTNTGeneratorUIE");
        GameRegistry.registerTileEntity(TileEntityPinkGenerator.class, "TileEntityPinkGeneratorUIE");
        GameRegistry.registerTileEntity(TileEntityNetherStarGenerator.class, "TileEntityNetherStarGeneratorUIE");

        lapisAetheriusRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new LapisAetheriusRenderer());

        PinkFuelHelper.scanRecipesForPinkFuel();
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
