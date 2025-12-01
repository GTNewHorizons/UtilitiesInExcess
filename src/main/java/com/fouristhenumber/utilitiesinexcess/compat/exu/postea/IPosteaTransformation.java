package com.fouristhenumber.utilitiesinexcess.compat.exu.postea;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public interface IPosteaTransformation {

    // Called in FMLPreInitializationEvent
    public void registerDummies();

    // Called in FMLInitializationEvent
    public default void registerTEDummies() {}

    public default void addItemRemappings(Map<String, Item> remappings) {}

    public default void addBlockRemappings(Map<String, Block> remappings) {}

    // Called in FMLPostInitializationEvent
    public void registerTransformations();
}
