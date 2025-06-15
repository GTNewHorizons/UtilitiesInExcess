package com.fouristhenumber.utilitiesinexcess;

import com.fouristhenumber.utilitiesinexcess.config.BlockConfig;
import com.fouristhenumber.utilitiesinexcess.utils.SoundEventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
    // Override CommonProxy methods here, if you want a different behaviour on the client (e.g. registering renders).
    // Don't forget to call the super methods as well.


    @Override
    public void init(FMLInitializationEvent event) {
        // There is no point listening for sound events on the server side
        if (BlockConfig.soundMuffler.enableSoundMuffler) {
            soundEventHandler = new SoundEventHandler();
            MinecraftForge.EVENT_BUS.register(soundEventHandler);
        }
        super.init(event);
    }
}
