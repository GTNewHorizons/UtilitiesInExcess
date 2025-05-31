package com.fouristhenumber.utilitiesinexcess.common.recipe;

import static com.fouristhenumber.utilitiesinexcess.common.blocks.ModBlocks.compressedCobblestone;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;

public class RecipeLoader {

    public static void run() {
        if (Loader.isModLoaded("dreamcraft")) return;
        loadCompressedCobblestoneRecipes();
    }

    private static void loadCompressedCobblestoneRecipes() {

        GameRegistry.addRecipe(
            new ItemStack(compressedCobblestone, 1, 0),
            "###",
            "###",
            "###",
            '#',
            new ItemStack(Blocks.cobblestone, 9, 0));

        GameRegistry
            .addShapelessRecipe(new ItemStack(Blocks.cobblestone, 9, 0), new ItemStack(compressedCobblestone, 1, 0));

        for (int i = 0; i < 7; i++) {
            GameRegistry.addRecipe(
                new ItemStack(compressedCobblestone, 1, i + 1),
                "###",
                "###",
                "###",
                '#',
                new ItemStack(compressedCobblestone, 1, i));

            GameRegistry.addShapelessRecipe(
                new ItemStack(compressedCobblestone, 9, i),
                new ItemStack(compressedCobblestone, 1, i + 1));
        }
    }
}
