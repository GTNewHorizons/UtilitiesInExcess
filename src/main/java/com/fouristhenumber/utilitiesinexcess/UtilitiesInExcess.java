package com.fouristhenumber.utilitiesinexcess;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.ForgeChunkManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockFilingCabinet.CabinetOrientationProperty;
import com.fouristhenumber.utilitiesinexcess.common.recipe.RecipeLoader;
import com.fouristhenumber.utilitiesinexcess.common.renderers.BlackoutCurtainsRenderer;
import com.fouristhenumber.utilitiesinexcess.common.renderers.LapisAetheriusRenderer;
import com.fouristhenumber.utilitiesinexcess.common.worldgen.WorldGenEnderLotus;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.fouristhenumber.utilitiesinexcess.compat.crafttweaker.EnderLocusCraftTweakerSupport;
import com.fouristhenumber.utilitiesinexcess.utils.PinkFuelHelper;
import com.fouristhenumber.utilitiesinexcess.utils.TEChunkLoadingCallback;
import com.gtnewhorizon.gtnhlib.blockstate.registry.BlockPropertyRegistry;

import cpw.mods.fml.client.registry.RenderingRegistry;
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
    dependencies = "required-after:gtnhlib@[0.9.9,);after:ForgeMicroblock;after:Waila;")
public class UtilitiesInExcess {

    public static final String MODID = "utilitiesinexcess";
    public static final String MODNAME = "Utilities in Excess";
    public static final Logger LOG = LogManager.getLogger(MODID);

    @Mod.Instance(MODID)
    public static UtilitiesInExcess uieInstance;

    public static int lapisAetheriusRenderID;
    public static int blackoutCurtainsRenderID;

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

        lapisAetheriusRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new LapisAetheriusRenderer());
        blackoutCurtainsRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new BlackoutCurtainsRenderer());

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

        if (ModBlocks.FILING_CABINET.isEnabled()) {
            BlockPropertyRegistry
                .registerBlockItemProperty(ModBlocks.FILING_CABINET.get(), CabinetOrientationProperty.instance);
        }

        if (ModBlocks.PINK_GENERATOR.isEnabled()) {
            PinkFuelHelper.scanRecipesForPinkFuel();
        }

        if (Mods.CraftTweaker.isLoaded()) {
            MineTweakerAPI.registerClass(EnderLocusCraftTweakerSupport.class);
        }

        ForgeChunkManager.setForcedChunkLoadingCallback(uieInstance, new TEChunkLoadingCallback());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
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

        public static final ItemStack ICON_ITEM = new ItemStack(ModItems.GOURMANDS_AXE.get());

        @Override
        public ItemStack getIconItemStack() {
            return ICON_ITEM;
        }
    };

    @Mod.EventHandler
    public void onMissingMapping(FMLMissingMappingsEvent event) {
        proxy.onMissingMapping(event);
    }
}
