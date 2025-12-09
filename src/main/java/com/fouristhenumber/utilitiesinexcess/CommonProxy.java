package com.fouristhenumber.utilitiesinexcess;

import org.lwjgl.input.Keyboard;

import com.fouristhenumber.utilitiesinexcess.client.IMCForNEI;
import com.fouristhenumber.utilitiesinexcess.common.dimensions.endoftime.EndOfTimeEvents;
import com.fouristhenumber.utilitiesinexcess.common.dimensions.underworld.UnderWorldEvents;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.fouristhenumber.utilitiesinexcess.compat.exu.Remappings;
import com.fouristhenumber.utilitiesinexcess.config.OtherConfig;
import com.fouristhenumber.utilitiesinexcess.network.PacketHandler;
import com.fouristhenumber.utilitiesinexcess.utils.SoundVolumeChecks;
import com.gtnewhorizon.gtnhlib.keybind.SyncedKeybind;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    public SoundVolumeChecks soundVolumeChecks;

    public SyncedKeybind GLOVE_KEYBIND;

    public void preInit(FMLPreInitializationEvent event) {
        // Config is handled in the early mixin loader (UIEMixinLoader)
        // since we want the config to be available
        // during mixin initialisation time.
        PacketHandler.init();
        ModBlocks.init();
        ModItems.init();
        ModDimensions.init();
        ModBiomes.init();
        UnderWorldEvents.init();
        EndOfTimeEvents.init();
        if (Mods.NEI.isLoaded()) {
            IMCForNEI.IMCSender();
        }
        if (OtherConfig.enableWorldConversion && !Mods.ExtraUtilities.isLoaded() && Mods.Postea.isLoaded()) {
            Remappings.preInit();
        }
    }

    public void init(FMLInitializationEvent event) {
        soundVolumeChecks = new SoundVolumeChecks();
        GLOVE_KEYBIND = SyncedKeybind.createConfigurable("key.uie.glove", "key.categories.uie", Keyboard.KEY_NONE);
    }

    public void postInit(FMLPostInitializationEvent event) {
        if (OtherConfig.enableWorldConversion && !Mods.ExtraUtilities.isLoaded() && Mods.Postea.isLoaded()) {
            Remappings.postInit();
        }
    }

    public void serverStarting(FMLServerStartingEvent event) {}
}
