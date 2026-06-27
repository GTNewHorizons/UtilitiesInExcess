package com.fouristhenumber.utilitiesinexcess;

import org.lwjgl.input.Keyboard;

import com.fouristhenumber.utilitiesinexcess.client.IMCForNEI;
import com.fouristhenumber.utilitiesinexcess.common.dimensions.endoftime.EndOfTimeEvents;
import com.fouristhenumber.utilitiesinexcess.common.dimensions.underworld.UnderWorldEvents;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemDestructionPickaxe;
import com.fouristhenumber.utilitiesinexcess.common.items.tools.ItemReversingHoe;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.FMPItems;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.fouristhenumber.utilitiesinexcess.compat.exu.PosteaTransforms;
import com.fouristhenumber.utilitiesinexcess.config.OtherConfig;
import com.fouristhenumber.utilitiesinexcess.network.PacketHandler;
import com.fouristhenumber.utilitiesinexcess.utils.SoundVolumeChecks;
import com.gtnewhorizon.gtnhlib.datastructs.space.ArrayProximityCheck4D;
import com.gtnewhorizon.gtnhlib.datastructs.space.VolumeShape;
import com.gtnewhorizon.gtnhlib.keybind.SyncedKeybind;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    public SoundVolumeChecks soundVolumeChecks;
    public ArrayProximityCheck4D mobSpawnBlockChecks = new ArrayProximityCheck4D(VolumeShape.CUBE);

    public SyncedKeybind GLOVE_KEYBIND;

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
        UnderWorldEvents.init();
        EndOfTimeEvents.init();
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
        }
    }

    public void init(FMLInitializationEvent event) {
        soundVolumeChecks = new SoundVolumeChecks();
        GLOVE_KEYBIND = SyncedKeybind.createConfigurable("key.uie.glove", "key.categories.uie", Keyboard.KEY_NONE);
    }

    public void postInit(FMLPostInitializationEvent event) {
        if (OtherConfig.enableWorldConversion && !Mods.ExtraUtilities.isLoaded() && Mods.Postea.isLoaded()) {
            PosteaTransforms.postInit();
        }

        ItemReversingHoe.initializeCache();
        ItemDestructionPickaxe.initializeCache();
    }

    public void serverStarting(FMLServerStartingEvent event) {}
}
