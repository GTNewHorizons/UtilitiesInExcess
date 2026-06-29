package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.fouristhenumber.utilitiesinexcess.config.OtherConfig;

import codechicken.microblock.BlockMicroMaterial;
import codechicken.microblock.MicroMaterialRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class FMPCompat {

    public static void init() {
        registerMicroblock(ModBlocks.SMART_PUMP);
        registerMicroblock(ModBlocks.TRADING_POST);
        registerMicroblock(ModBlocks.COMPRESSED_COBBLESTONE, 0, 7);
        registerMicroblock(ModBlocks.COMPRESSED_DIRT, 0, 7);
        registerMicroblock(ModBlocks.DECORATIVE_BLOCKS, 0, 11);
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
        registerMicroblock(ModBlocks.DECORATIVE_GLASS, 0, 12);
        registerMicroblock(ModBlocks.ENDSPARK);
        registerMicroblock(ModBlocks.INVERTED_BLOCK);
        registerMicroblock(ModBlocks.BEDROCKIUM_BLOCK);
        registerMicroblock(ModBlocks.MAGIC_WOOD);
        registerMicroblock(ModBlocks.ETHEREAL_GLASS, 0, 5);
        registerMicroblock(ModBlocks.LAPIS_AETHERIUS, 0, 15);

        if (Mods.ExtraUtilities.isLoaded() || !OtherConfig.enableWorldConversion) return;

        registerRemap("ExtraUtilities:enderThermicPump", ModBlocks.SMART_PUMP);
        registerRemap("ExtraUtilities:trading_post", ModBlocks.TRADING_POST);
        registerRemap("tile.extrautils:cobblestone_compressed", ModBlocks.COMPRESSED_COBBLESTONE, 7);
        registerRemap("tile.extrautils:cobblestone_compressed", ModBlocks.COMPRESSED_DIRT, 8, 0);
        registerRemap("tile.extrautils:cobblestone_compressed", ModBlocks.COMPRESSED_DIRT, 9, 1);
        registerRemap("tile.extrautils:cobblestone_compressed", ModBlocks.COMPRESSED_DIRT, 10, 2);
        registerRemap("tile.extrautils:cobblestone_compressed", ModBlocks.COMPRESSED_DIRT, 11, 3);
        registerRemap("tile.extrautils:cobblestone_compressed", ModBlocks.COMPRESSED_GRAVEL, 12, 0);
        registerRemap("tile.extrautils:cobblestone_compressed", ModBlocks.COMPRESSED_GRAVEL, 13, 1);
        registerRemap("tile.extrautils:cobblestone_compressed", ModBlocks.COMPRESSED_SAND, 14, 0);
        registerRemap("tile.extrautils:cobblestone_compressed", ModBlocks.COMPRESSED_SAND, 15, 1);
        registerRemap("tile.extrautils:colorStoneBrick", ModBlocks.COLORED_STONE_BRICKS, 15);
        registerRemap("tile.extrautils:colorWoodPlanks", ModBlocks.COLORED_WOOD_PLANKS, 15);
        registerRemap("tile.extrautils:color_lightgem", ModBlocks.COLORED_GLOWSTONE, 15);
        registerRemap("tile.extrautils:color_stone", ModBlocks.COLORED_STONE, 15);
        registerRemap("tile.extrautils:color_quartzBlock", ModBlocks.COLORED_QUARTZ_BLOCK, 15);
        registerRemap("tile.extrautils:color_hellsand", ModBlocks.COLORED_SOUL_SAND, 15);
        registerRemap("tile.extrautils:color_redstoneLight", ModBlocks.COLORED_REDSTONE_LAMP, 15);
        registerRemap("tile.extrautils:color_brick", ModBlocks.COLORED_BRICKS, 15);
        registerRemap("tile.extrautils:color_stonebrick", ModBlocks.COLORED_COBBLESTONE, 15);
        registerRemap("tile.extrautils:color_blockLapis", ModBlocks.COLORED_LAPIS_BLOCK, 15);
        registerRemap("tile.extrautils:color_obsidian", ModBlocks.COLORED_OBSIDIAN, 15);
        registerRemap("tile.extrautils:color_blockRedstone", ModBlocks.COLORED_REDSTONE_BLOCK, 15);
        registerRemap("tile.extrautils:color_blockCoal", ModBlocks.COLORED_COAL_BLOCK, 15);
        registerRemap("tile.extrautils:decorativeBlock1", ModBlocks.DECORATIVE_BLOCKS, 0, 7);
        registerRemap("tile.extrautils:decorativeBlock1", ModBlocks.DECORATIVE_BLOCKS, 1, 5);
        registerRemap("tile.extrautils:decorativeBlock1", ModBlocks.DECORATIVE_BLOCKS, 2, 2);
        registerRemap("tile.extrautils:decorativeBlock1", ModBlocks.DECORATIVE_BLOCKS, 3, 4);
        registerRemap("tile.extrautils:decorativeBlock1", ModBlocks.DECORATIVE_BLOCKS, 4, 8);
        registerRemap("tile.extrautils:decorativeBlock1", ModBlocks.DECORATIVE_BLOCKS, 6, 6);
        registerRemap("tile.extrautils:decorativeBlock1", ModBlocks.DECORATIVE_BLOCKS, 7, 9);
        registerRemap("tile.extrautils:decorativeBlock1", ModBlocks.DECORATIVE_BLOCKS, 9, 3);
        registerRemap("tile.extrautils:decorativeBlock1", ModBlocks.DECORATIVE_BLOCKS, 10, 10);
        registerRemap("tile.extrautils:decorativeBlock1", ModBlocks.DECORATIVE_BLOCKS, 12, 0);
        registerRemap("tile.extrautils:decorativeBlock1", ModBlocks.DECORATIVE_BLOCKS, 13, 1);
        registerRemap("tile.extrautils:decorativeBlock1", ModBlocks.DECORATIVE_BLOCKS, 14, 11);
        registerRemap("tile.extrautils:decorativeBlock2", ModBlocks.DECORATIVE_GLASS, 11);
        registerRemap("tile.extrautils:decorativeBlock1_11", ModBlocks.ENDSPARK);
        registerRemap("tile.extrautils:decorativeBlock1_5", ModBlocks.INVERTED_BLOCK);
        registerRemap("tile.extrautils:block_bedrockium", ModBlocks.BEDROCKIUM_BLOCK);
        registerRemap("tile.extrautils:decorativeBlock1_8", ModBlocks.MAGIC_WOOD);
        registerRemap("tile.extrautils:etherealglass", ModBlocks.ETHEREAL_GLASS, 5);
        registerRemap("tile.extrautils:greenscreen", ModBlocks.LAPIS_AETHERIUS, 15);
    }

    private static void registerMicroblock(ModBlocks block, int meta) {
        String blockName = GameRegistry.findUniqueIdentifierFor(block.get())
            .toString();
        if (block.isEnabled()) {
            MicroMaterialRegistry
                .registerMaterial(new BlockMicroMaterial(block.get(), meta), applyMeta(blockName, meta));
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

    private static void registerRemap(String exuName, ModBlocks uieBlock) {
        MicroMaterialRegistry.remapName(
            exuName,
            GameRegistry.findUniqueIdentifierFor(uieBlock.get())
                .toString());
    }

    private static void registerRemap(String exuName, ModBlocks uieBlock, int max) {
        String blockName = GameRegistry.findUniqueIdentifierFor(uieBlock.get())
            .toString();
        for (int m = 0; m <= max; m++) {
            MicroMaterialRegistry.remapName(applyMeta(exuName, m), applyMeta(blockName, m));
        }
    }

    private static void registerRemap(String exuName, ModBlocks uieBlock, int exuMeta, int uieMeta) {
        MicroMaterialRegistry.remapName(
            applyMeta(exuName, exuMeta),
            applyMeta(
                GameRegistry.findUniqueIdentifierFor(uieBlock.get())
                    .toString(),
                uieMeta));
    }

    private static String applyMeta(String name, int meta) {
        if (meta == 0) return name;
        return name + "_" + meta;
    }
}
