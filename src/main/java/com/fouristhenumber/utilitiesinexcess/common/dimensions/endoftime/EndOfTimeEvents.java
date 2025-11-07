package com.fouristhenumber.utilitiesinexcess.common.dimensions.endoftime;

import net.minecraftforge.event.world.WorldEvent;

import com.fouristhenumber.utilitiesinexcess.config.dimensions.EndOfTimeConfig;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/// Various event hooks for the under world
public class EndOfTimeEvents {

    public static final EndOfTimeEvents INSTANCE = new EndOfTimeEvents();

    public static void init() {
        FMLCommonHandler.instance()
            .bus()
            .register(INSTANCE);
    }

    @SubscribeEvent
    public void disableSpawning(WorldEvent.PotentialSpawns event) {
        if (!EndOfTimeConfig.endOfTimeSpawning) event.setCanceled(true);
    }

}
