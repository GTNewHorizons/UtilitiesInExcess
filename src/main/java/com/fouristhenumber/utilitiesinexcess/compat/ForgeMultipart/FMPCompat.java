package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;

import codechicken.microblock.BlockMicroMaterial;
import codechicken.microblock.MicroMaterialRegistry;

public class FMPCompat {

    public static void init() {
        registerMicroblock(ModBlocks.SMART_PUMP);
        registerMicroblock(ModBlocks.TRADING_POST);
        registerMicroblock(ModBlocks.COMPRESSED_COBBLESTONE, 0, 7);
        registerMicroblock(ModBlocks.COMPRESSED_DIRT, 0, 7);
        registerMicroblock(ModBlocks.COMPRESSED_GRAVEL, 0, 7);
        registerMicroblock(ModBlocks.COMPRESSED_SAND, 0, 7);
        registerMicroblock(ModBlocks.COLORED_STONE_BRICKS, 0, 15);
        registerMicroblock(ModBlocks.COLORED_WOOD_PLANKS, 0, 15);
        registerMicroblock(ModBlocks.COLORED_GLOWSTONE, 0, 15);
        registerMicroblock(ModBlocks.COLORED_STONE, 0, 15);
        registerMicroblock(ModBlocks.COLORED_QUARTZ_BLOCK, 0, 15);
        registerMicroblock(ModBlocks.COLORED_SOUL_SAND, 0, 15);
        registerMicroblock(ModBlocks.COLORED_REDSTONE_LAMP, 0, 15);
        registerMicroblock(ModBlocks.COLORED_BRICKS, 0, 15);
        registerMicroblock(ModBlocks.COLORED_COBBLESTONE, 0, 15);
        registerMicroblock(ModBlocks.COLORED_LAPIS_BLOCK, 0, 15);
        registerMicroblock(ModBlocks.COLORED_OBSIDIAN, 0, 15);
        registerMicroblock(ModBlocks.COLORED_REDSTONE_BLOCK, 0, 15);
        registerMicroblock(ModBlocks.COLORED_COAL_BLOCK, 0, 15);
        registerMicroblock(ModBlocks.DECORATIVE_BLOCKS, 0, 11);
        registerMicroblock(ModBlocks.DECORATIVE_GLASS, 0, 12);
        registerMicroblock(ModBlocks.ENDSPARK);
        registerMicroblock(ModBlocks.INVERTED_BLOCK);
        registerMicroblock(ModBlocks.BEDROCKIUM_BLOCK);
        registerMicroblock(ModBlocks.MAGIC_WOOD);
        registerMicroblock(ModBlocks.ETHEREAL_GLASS, 0, 5);
        registerMicroblock(ModBlocks.LAPIS_AETHERIUS, 0, 15);
    }

    private static void registerMicroblock(ModBlocks block, int meta) {
        if (block.isEnabled()) {
            MicroMaterialRegistry.registerMaterial(
                new BlockMicroMaterial(block.get(), meta),
                block.get()
                    .getUnlocalizedName());
        }
    }

    private static void registerMicroblock(ModBlocks block) {
        registerMicroblock(block, 0);
    }

    private static void registerMicroblock(ModBlocks block, int min, int max) {
        for (int m = min; m <= max; m++) {
            registerMicroblock(block, m);
        }
    }
}
