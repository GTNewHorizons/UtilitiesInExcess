package com.fouristhenumber.utilitiesinexcess.common.blocks;

import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks {

    public static BlockFloating floatingBlock;
    public static BlockCompressedCobblestone compressedCobblestone;

    public static void init() {
        floatingBlock = new BlockFloating();
        GameRegistry.registerBlock(floatingBlock, BlockFloating.ItemBlockFloating.class, "floatingBlock");

        compressedCobblestone = new BlockCompressedCobblestone();
        GameRegistry.registerBlock(
            compressedCobblestone,
            BlockCompressedCobblestone.ItemCompressedCobblestone.class,
            "compressedCobblestone");
    }
}
