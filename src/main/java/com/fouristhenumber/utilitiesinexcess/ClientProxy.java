package com.fouristhenumber.utilitiesinexcess;

import net.minecraftforge.common.MinecraftForge;

import com.fouristhenumber.utilitiesinexcess.common.renderers.WireframeRenderer;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {

    // Override CommonProxy methods here, if you want a different behaviour on the client (e.g. registering renders).
    // Don't forget to call the super methods as well.

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        
        MinecraftForge.EVENT_BUS.register(new WireframeRenderer());
    }
}
