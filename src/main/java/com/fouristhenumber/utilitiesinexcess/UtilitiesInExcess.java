package com.fouristhenumber.utilitiesinexcess;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fouristhenumber.utilitiesinexcess.common.recipe.RecipeLoader;
import com.fouristhenumber.utilitiesinexcess.common.renderers.BlackoutCurtainsRenderer;
import com.fouristhenumber.utilitiesinexcess.common.renderers.LapisAetheriusRenderer;
import com.fouristhenumber.utilitiesinexcess.common.renderers.SpikeRenderer;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityBlockUpdateDetector;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityConveyor;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityDrum;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityMarginallyMaximisedChest;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityPacifistsBench;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityPortalUnderWorld;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityPureLove;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityRadicallyReducedChest;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityRainMuffler;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityRedstoneClock;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntitySignificantlyShrunkChest;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntitySmartPump;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntitySoundMuffler;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntitySpike;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityTradingPost;
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
import com.fouristhenumber.utilitiesinexcess.common.worldgen.WorldGenEnderLotus;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.fouristhenumber.utilitiesinexcess.compat.crafttweaker.QEDCraftTweakerSupport;
import com.fouristhenumber.utilitiesinexcess.compat.exu.ExuCompat;
import com.fouristhenumber.utilitiesinexcess.compat.exu.Remappings;
import com.fouristhenumber.utilitiesinexcess.compat.tinkers.TinkersCompat;
import com.fouristhenumber.utilitiesinexcess.config.OtherConfig;
import com.fouristhenumber.utilitiesinexcess.utils.FMLEventHandler;
import com.fouristhenumber.utilitiesinexcess.utils.ForgeEventHandler;
import com.fouristhenumber.utilitiesinexcess.utils.PinkFuelHelper;
import com.fouristhenumber.utilitiesinexcess.utils.PumpChunkLoadingCallback;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import minetweaker.MineTweakerAPI;

@Mod(
    modid = UtilitiesInExcess.MODID,
    version = Tags.VERSION,
    name = "UtilitiesInExcess",
    acceptedMinecraftVersions = "[1.7.10]",
    dependencies = "required-after:gtnhlib@[0.6.31,)")
public class UtilitiesInExcess {

    public static final String MODID = "utilitiesinexcess";
    public static final Logger LOG = LogManager.getLogger(MODID);

    @Mod.Instance(MODID)
    public static UtilitiesInExcess uieInstance;

    public static int lapisAetheriusRenderID;
    public static int blackoutCurtainsRenderID;
    public static int spikeRenderID;

    @SidedProxy(
        clientSide = "com.fouristhenumber.utilitiesinexcess.ClientProxy",
        serverSide = "com.fouristhenumber.utilitiesinexcess.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.registerTileEntity(TileEntitySpike.class, "utilitiesinexcess:TileEntitySpike");
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);

        RecipeLoader.run();

        MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());
        FMLCommonHandler.instance()
            .bus()
            .register(new FMLEventHandler());

        GameRegistry.registerTileEntity(TileEntityRedstoneClock.class, "TileEntityRedstoneClockUIE");
        GameRegistry.registerTileEntity(TileEntityTrashCanItem.class, "TileEntityTrashCanItemUIE");
        GameRegistry.registerTileEntity(TileEntityTrashCanFluid.class, "TileEntityTrashCanFluidUIE");
        GameRegistry.registerTileEntity(TileEntityTrashCanEnergy.class, "TileEntityTrashCanEnergyUIE");
        GameRegistry.registerTileEntity(TileEntityDrum.class, "TileEntityDrumUIE");
        GameRegistry.registerTileEntity(TileEntityPureLove.class, "TileEntityPureLoveUIE");
        GameRegistry
            .registerTileEntity(TileEntityMarginallyMaximisedChest.class, "TileEntityMarginallyMaximisedChestUIE");
        GameRegistry
            .registerTileEntity(TileEntitySignificantlyShrunkChest.class, "TileEntitySignificantlyShrunkChestUIE");
        GameRegistry.registerTileEntity(TileEntityRadicallyReducedChest.class, "TileEntityRadicallyReducedChestUIE");
        GameRegistry.registerTileEntity(TileEntitySoundMuffler.class, "TileEntitySoundMufflerUIE");
        GameRegistry.registerTileEntity(TileEntityRainMuffler.class, "TileEntityRainMufflerUIE");
        GameRegistry.registerTileEntity(TileEntityBlockUpdateDetector.class, "TileEntityBlockUpdateDetector");
        GameRegistry.registerTileEntity(TileEntityConveyor.class, "TileEntityConveyor");
        GameRegistry.registerTileEntity(TileEntityPortalUnderWorld.class, "TileEntityPortalUnderWorld");
        GameRegistry.registerTileEntity(TileEntityBlockUpdateDetector.class, "TileEntityBlockUpdateDetectorUIE");
        GameRegistry.registerTileEntity(TileEntityConveyor.class, "TileEntityConveyorUIE");
        GameRegistry.registerTileEntity(TileEntityPortalUnderWorld.class, "TileEntityPortalUnderWorldUIE");
        GameRegistry.registerTileEntity(TileEntitySmartPump.class, "TileEntitySmartPumpUIE");
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
        GameRegistry.registerTileEntity(TileEntityPacifistsBench.class, "TileEntityPacifistsBenchUIE");
        GameRegistry.registerTileEntity(TileEntityTradingPost.class, "TileEntityTradingPostUIE");
        if (OtherConfig.enableWorldConversion && !Mods.ExtraUtilities.isLoaded() && Mods.Postea.isLoaded()) {
            Remappings.init();
        }

        lapisAetheriusRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new LapisAetheriusRenderer());
        blackoutCurtainsRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new BlackoutCurtainsRenderer());
        spikeRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new SpikeRenderer());

        GameRegistry.registerWorldGenerator(new WorldGenEnderLotus(), 10);

        if (ModItems.ENDER_LOTUS_SEED.isEnabled()) {
            ChestGenHooks.addItem(
                ChestGenHooks.DUNGEON_CHEST,
                new WeightedRandomChestContent(ModItems.ENDER_LOTUS_SEED.get(), 0, 1, 2, 8));
        }
        if (ModItems.INVERSION_SIGIL_INACTIVE.isEnabled()) {
            ChestGenHooks.addItem(
                ChestGenHooks.DUNGEON_CHEST,
                new WeightedRandomChestContent(ModItems.INVERSION_SIGIL_INACTIVE.get(), 0, 1, 1, 2));
            ChestGenHooks.addItem(
                ChestGenHooks.MINESHAFT_CORRIDOR,
                new WeightedRandomChestContent(ModItems.INVERSION_SIGIL_INACTIVE.get(), 0, 1, 1, 1));
            ChestGenHooks.addItem(
                ChestGenHooks.PYRAMID_DESERT_CHEST,
                new WeightedRandomChestContent(ModItems.INVERSION_SIGIL_INACTIVE.get(), 0, 1, 1, 2));
            ChestGenHooks.addItem(
                ChestGenHooks.STRONGHOLD_CORRIDOR,
                new WeightedRandomChestContent(ModItems.INVERSION_SIGIL_INACTIVE.get(), 0, 1, 1, 2));
            ChestGenHooks.addItem(
                ChestGenHooks.STRONGHOLD_CROSSING,
                new WeightedRandomChestContent(ModItems.INVERSION_SIGIL_INACTIVE.get(), 0, 1, 1, 1));
        }

        if (ModBlocks.SMART_PUMP.isEnabled()) {
            ForgeChunkManager.setForcedChunkLoadingCallback(uieInstance, new PumpChunkLoadingCallback());
        }

        if (ModBlocks.PINK_GENERATOR.isEnabled()) {
            PinkFuelHelper.scanRecipesForPinkFuel();
        }

        if (Mods.CraftTweaker.isLoaded()) {
            MineTweakerAPI.registerClass(QEDCraftTweakerSupport.class);
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);

        if (Mods.Tinkers.isLoaded() && OtherConfig.enableTinkersIntegration) {
            TinkersCompat.init();
        }
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

    public static CreativeTabs uieTab = new CreativeTabs(MODID) {

        @Override
        public Item getTabIconItem() {
            return this.getIconItemStack()
                .getItem();
        }

        public static final ItemStack ICON_ITEM = new ItemStack(ModItems.GLUTTONS_AXE.get());

        @Override
        public ItemStack getIconItemStack() {
            return ICON_ITEM;
        }
    };

    @Mod.EventHandler
    public void onMissingMappings(FMLMissingMappingsEvent event) {
        ExuCompat.onMissingMappings(event);
    }
}
