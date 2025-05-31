package com.fouristhenumber.utilitiesinexcess.common.recipe;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;

import cpw.mods.fml.common.registry.GameRegistry;

public class RecipeLoader {

    public static void run() {
        if (Mods.Dreamcraft.isLoaded()) return;
        loadCompressedCobblestoneRecipes();
    }

    private static void loadCompressedCobblestoneRecipes() {

        GameRegistry.addRecipe(
            new ItemStack(ModBlocks.COMPRESSED_COBBLESTONE.get(), 1, 0),
            "###",
            "###",
            "###",
            '#',
            new ItemStack(Blocks.cobblestone, 9, 0));

        GameRegistry.addShapelessRecipe(
            new ItemStack(Blocks.cobblestone, 9, 0),
            new ItemStack(ModBlocks.COMPRESSED_COBBLESTONE.get(), 1, 0));

        for (int i = 0; i < 7; i++) {
            GameRegistry.addRecipe(
                new ItemStack(ModBlocks.COMPRESSED_COBBLESTONE.get(), 1, i + 1),
                "###",
                "###",
                "###",
                '#',
                new ItemStack(ModBlocks.COMPRESSED_COBBLESTONE.get(), 1, i));

            GameRegistry.addShapelessRecipe(
                new ItemStack(ModBlocks.COMPRESSED_COBBLESTONE.get(), 9, i),
                new ItemStack(ModBlocks.COMPRESSED_COBBLESTONE.get(), 1, i + 1));
        }
    }
}
