package com.fouristhenumber.utilitiesinexcess;

import com.fouristhenumber.utilitiesinexcess.client.IMCForNEI;
import com.fouristhenumber.utilitiesinexcess.common.dimensions.endoftime.EndOfTimeEvents;
import com.fouristhenumber.utilitiesinexcess.common.dimensions.underworld.UnderWorldEvents;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.fouristhenumber.utilitiesinexcess.network.PacketHandler;
import com.fouristhenumber.utilitiesinexcess.utils.SoundVolumeChecks;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    public SoundVolumeChecks soundVolumeChecks;

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
    }

    public void init(FMLInitializationEvent event) {
        soundVolumeChecks = new SoundVolumeChecks();
        ModTileEntities.init();
        if (Mods.Waila.isLoaded()) {
            FMLInterModComms.sendMessage(
                "Waila",
                "register",
                "com.fouristhenumber.utilitiesinexcess.compat.waila.WailaHandler.callbackRegister");
        }
    }

    public void postInit(FMLPostInitializationEvent event) {}

    public void serverStarting(FMLServerStartingEvent event) {}
}
