package com.fouristhenumber.utilitiesinexcess;

import net.minecraftforge.oredict.OreDictionary;

import com.fouristhenumber.utilitiesinexcess.config.OtherConfig;

public class ModOreDictionary {

    public static void init() {
        // Items
        OreDictionary.registerOre("ingotBedrockium", ModItems.BEDROCKIUM_INGOT.get());
        OreDictionary
            .registerOre("ingotUnstable", ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE));
        OreDictionary
            .registerOre("ingotInverted", ModItems.INVERTED_INGOT.newItemStack(1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("nuggetUnstable", ModItems.INVERTED_NUGGET.get());
        OreDictionary.registerOre("nuggetInverted", ModItems.INVERTED_NUGGET.get());
        OreDictionary.registerOre("craftingToolShears", ModItems.PRECISION_SHEARS.get());

        // Blocks
        OreDictionary.registerOre("burntQuartz", ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 2));
        OreDictionary.registerOre("blockIcestone", ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 4));
        OreDictionary.registerOre("bricksGravel", ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 6));
        OreDictionary.registerOre("blockGlassSandy", ModBlocks.DECORATIVE_BLOCKS.newItemStack(1, 3));
        OreDictionary.registerOre("blockMagicWood", ModBlocks.MAGIC_WOOD.get());
        OreDictionary.registerOre("blockBedrockium", ModBlocks.BEDROCKIUM_BLOCK.get());
        OreDictionary.registerOre("blockUnstable", ModBlocks.INVERTED_BLOCK.get());
        OreDictionary.registerOre("blockInverted", ModBlocks.INVERTED_BLOCK.get());
        // TODO: Endspark after merge
        // OreDictionary.registerOre("blockEnderCore", ModItems.ENDSPARK.get());

        // Only registers the non-colored decorative glass, for parity with ExU.
        int[] glassMetas = { 0, 1, 2, 3, 6, 9, 12 };
        for (int meta : glassMetas) {
            OreDictionary.registerOre("blockGlass", ModBlocks.DECORATIVE_GLASS.newItemStack(1, meta));
        }
        for (int i = 0; i < 9; i++) {
            OreDictionary.registerOre(
                "compressedCobblestone" + (i + 1) + "x",
                ModBlocks.COMPRESSED_COBBLESTONE.newItemStack(1, i));
            OreDictionary.registerOre("compressedDirt" + (i + 1) + "x", ModBlocks.COMPRESSED_DIRT.newItemStack(1, i));
            OreDictionary.registerOre("compressedSand" + (i + 1) + "x", ModBlocks.COMPRESSED_SAND.newItemStack(1, i));
            OreDictionary
                .registerOre("compressedGravel" + (i + 1) + "x", ModBlocks.COMPRESSED_GRAVEL.newItemStack(1, i));
        }
        if (OtherConfig.coloredBlockOredict) {
            OreDictionary.registerOre(
                "bricksStone",
                ModBlocks.COLORED_STONE_BRICKS.newItemStack(1, OreDictionary.WILDCARD_VALUE));
            OreDictionary
                .registerOre("plankWood", ModBlocks.COLORED_WOOD_PLANKS.newItemStack(1, OreDictionary.WILDCARD_VALUE));
            OreDictionary
                .registerOre("glowstone", ModBlocks.COLORED_GLOWSTONE.newItemStack(1, OreDictionary.WILDCARD_VALUE));
            OreDictionary.registerOre("stone", ModBlocks.COLORED_STONE.newItemStack(1, OreDictionary.WILDCARD_VALUE));
            OreDictionary
                .registerOre("stoneSmooth", ModBlocks.COLORED_STONE.newItemStack(1, OreDictionary.WILDCARD_VALUE));
            OreDictionary
                .registerOre("soulsand", ModBlocks.COLORED_SOUL_SAND.newItemStack(1, OreDictionary.WILDCARD_VALUE));
            OreDictionary
                .registerOre("bricksClay", ModBlocks.COLORED_BRICKS.newItemStack(1, OreDictionary.WILDCARD_VALUE));
            OreDictionary.registerOre(
                "stoneCobble",
                ModBlocks.COLORED_COBBLESTONE.newItemStack(1, OreDictionary.WILDCARD_VALUE));
            OreDictionary.registerOre(
                "cobblestone",
                ModBlocks.COLORED_COBBLESTONE.newItemStack(1, OreDictionary.WILDCARD_VALUE));
            OreDictionary
                .registerOre("blockLapis", ModBlocks.COLORED_LAPIS_BLOCK.newItemStack(1, OreDictionary.WILDCARD_VALUE));
            OreDictionary
                .registerOre("obsidian", ModBlocks.COLORED_OBSIDIAN.newItemStack(1, OreDictionary.WILDCARD_VALUE));
            OreDictionary.registerOre(
                "blockRedstone",
                ModBlocks.COLORED_REDSTONE_BLOCK.newItemStack(1, OreDictionary.WILDCARD_VALUE));
            OreDictionary.registerOre(
                "blockAnyCarbon",
                ModBlocks.COLORED_COAL_BLOCK.newItemStack(1, OreDictionary.WILDCARD_VALUE));
            OreDictionary
                .registerOre("blockCoal", ModBlocks.COLORED_COAL_BLOCK.newItemStack(1, OreDictionary.WILDCARD_VALUE));
        }
    }
}
