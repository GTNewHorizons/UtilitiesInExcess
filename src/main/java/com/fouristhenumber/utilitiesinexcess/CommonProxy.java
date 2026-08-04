package com.fouristhenumber.utilitiesinexcess;

import codechicken.lib.world.TileChunkLoadHook;
import com.fouristhenumber.utilitiesinexcess.common.renderers.transfer.EnergyNodeRenderer;
import com.fouristhenumber.utilitiesinexcess.common.renderers.transfer.TransferNodeRenderer;
import com.fouristhenumber.utilitiesinexcess.common.renderers.transfer.TransferPipeRenderer;
import com.fouristhenumber.utilitiesinexcess.utils.ColoredSlots;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.ForgeChunkManager;

import org.lwjgl.input.Keyboard;

import com.fouristhenumber.utilitiesinexcess.client.IMCForNEI;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockColored;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockFilingCabinet;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemInversionSigilInactive;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemErasurePickaxe;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemRetrogradeHoe;
import com.fouristhenumber.utilitiesinexcess.common.recipe.RecipeLoader;
import com.fouristhenumber.utilitiesinexcess.common.renderers.BlackoutCurtainsRenderer;
import com.fouristhenumber.utilitiesinexcess.common.renderers.LapisAetheriusRenderer;
import com.fouristhenumber.utilitiesinexcess.common.worldgen.WorldGenEnderLotus;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.FMPCompat;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.FMPItems;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Content;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.fouristhenumber.utilitiesinexcess.compat.crafttweaker.EnderLocusCraftTweakerSupport;
import com.fouristhenumber.utilitiesinexcess.compat.exu.ExuWorldConversionWarning;
import com.fouristhenumber.utilitiesinexcess.compat.exu.PosteaTransforms;
import com.fouristhenumber.utilitiesinexcess.compat.tinkers.TinkersCompat;
import com.fouristhenumber.utilitiesinexcess.config.OtherConfig;
import com.fouristhenumber.utilitiesinexcess.config.blocks.ColoredBlocksConfig;
import com.fouristhenumber.utilitiesinexcess.network.PacketHandler;
import com.fouristhenumber.utilitiesinexcess.utils.PinkFuelHelper;
import com.fouristhenumber.utilitiesinexcess.utils.SoundVolumeChecks;
import com.fouristhenumber.utilitiesinexcess.utils.TEChunkLoadingCallback;
import com.gtnewhorizon.gtnhlib.api.gui.WorldConversionWarningManager;
import com.gtnewhorizon.gtnhlib.blockstate.registry.BlockPropertyRegistry;
import com.gtnewhorizon.gtnhlib.datastructs.space.ArrayProximityCheck4D;
import com.gtnewhorizon.gtnhlib.datastructs.space.VolumeShape;
import com.gtnewhorizon.gtnhlib.keybind.SyncedKeybind;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import minetweaker.MineTweakerAPI;

public class CommonProxy {

    public static int lapisAetheriusRenderID;
    public static int blackoutCurtainsRenderID;
    public static int spikeRenderID;
    public static int transferPipeRenderID;
    public static int energyNodeRenderID;
    public static int flatNodeRenderID;
    public SoundVolumeChecks soundVolumeChecks;
    public ArrayProximityCheck4D mobSpawnBlockChecks = new ArrayProximityCheck4D(VolumeShape.CUBE);

    public SyncedKeybind GLOVE_KEYBIND;
    public SyncedKeybind BUILDERS_KEYBIND_H;
    public SyncedKeybind BUILDERS_KEYBIND_V;

    public void preInit(FMLPreInitializationEvent event) {
        // Config is handled in the early mixin loader (UIEMixinLoader)
        // since we want the config to be available
        // during mixin initialisation time.
        PacketHandler.init();
        ModBlocks.init();
        ModItems.init();
        ModOreDictionary.init();
        ModDimensions.init();
        ModBiomes.init();
        TileChunkLoadHook.init();
        ColoredSlots.init();

        transferPipeRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new TransferPipeRenderer());
        flatNodeRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new TransferNodeRenderer());
        energyNodeRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new EnergyNodeRenderer());

        GameRegistry.registerWorldGenerator(new WorldGenEnderLotus(), 10);

        ForgeChunkManager.setForcedChunkLoadingCallback(UtilitiesInExcess.uieInstance, new TEChunkLoadingCallback());

        if (ModBlocks.LAPIS_AETHERIUS.isEnabled()) {
            CommonProxy.lapisAetheriusRenderID = RenderingRegistry.getNextAvailableRenderId();
            RenderingRegistry.registerBlockHandler(new LapisAetheriusRenderer());
        }

        if (ModBlocks.BLACKOUT_CURTAINS.isEnabled()) {
            CommonProxy.blackoutCurtainsRenderID = RenderingRegistry.getNextAvailableRenderId();
            RenderingRegistry.registerBlockHandler(new BlackoutCurtainsRenderer());
        }

        if (Mods.NEI.isLoaded()) {
            IMCForNEI.IMCSender();
        }

        if (Mods.Waila.isLoaded()) {
            FMLInterModComms.sendMessage(
                "Waila",
                "register",
                "com.fouristhenumber.utilitiesinexcess.compat.waila.WailaCompat.callbackRegister");
        }

        if (Mods.ForgeMicroBlock.isLoaded()) {
            FMPItems.init();
            new Content().init();
            FMPCompat.init();
        }

        if (ColoredBlocksConfig.INSTANCE.enableColoredBlocks) {
            BlockColored.registerConfigBlocks();
        }

        if (OtherConfig.enableWorldConversionWarning) {
            WorldConversionWarningManager.register(UtilitiesInExcess.MODID + "_EXU", new ExuWorldConversionWarning());
        }

        if (ModItems.ENDER_LOTUS_SEED.isEnabled()) {
            ChestGenHooks.addItem(
                ChestGenHooks.DUNGEON_CHEST,
                new WeightedRandomChestContent(ModItems.ENDER_LOTUS_SEED.get(), 0, 1, 2, 8));
        }

        if (ModItems.INVERSION_SIGIL_INACTIVE.isEnabled()) {
            ItemInversionSigilInactive.registerChestLoot();
        }

        if (ModBlocks.FILING_CABINET.isEnabled()) {
            BlockPropertyRegistry.registerBlockItemProperty(
                ModBlocks.FILING_CABINET.get(),
                BlockFilingCabinet.CabinetOrientationProperty.instance);
        }

        if (ModBlocks.PINK_GENERATOR.isEnabled()) {
            PinkFuelHelper.scanRecipesForPinkFuel();
        }

        if (Mods.CraftTweaker.isLoaded()) {
            MineTweakerAPI.registerClass(EnderLocusCraftTweakerSupport.class);
        }

        RecipeLoader.run();
    }

    public void init(FMLInitializationEvent event) {
        soundVolumeChecks = new SoundVolumeChecks();
        GLOVE_KEYBIND = SyncedKeybind.createConfigurable("uie.key.glove", "uie.key.categories.uie", Keyboard.KEY_NONE);
        BUILDERS_KEYBIND_H = SyncedKeybind.createFromMC(() -> () -> Minecraft.getMinecraft().gameSettings.keyBindSneak);
        BUILDERS_KEYBIND_V = SyncedKeybind
            .createFromMC(() -> () -> Minecraft.getMinecraft().gameSettings.keyBindSprint);
        ModTileEntities.init();
    }

    public void postInit(FMLPostInitializationEvent event) {
        if (OtherConfig.enableWorldConversion && !Mods.ExtraUtilities.isLoaded() && Mods.Postea.isLoaded()) {
            PosteaTransforms.postInit();
        }

        ItemRetrogradeHoe.initializeCache();
        ItemErasurePickaxe.initializeCache();
        if (Mods.Tinkers.isLoaded() && OtherConfig.enableTinkersIntegration) {
            TinkersCompat.init();
        }
    }

    public void loadComplete(FMLLoadCompleteEvent event) {

        if (ColoredBlocksConfig.INSTANCE.enableColoredBlocks) {
            BlockColored.initColoredBlocks();
        }

    }

    public void serverStarting(FMLServerStartingEvent event) {}

    public void onMissingMapping(FMLMissingMappingsEvent event) {
        ExuWorldConversionWarning.onMissingMapping(event);
    }

}
