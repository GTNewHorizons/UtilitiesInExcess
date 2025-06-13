package com.fouristhenumber.utilitiesinexcess.common.recipe;

import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockCompressed;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;

import cpw.mods.fml.common.registry.GameRegistry;

public class RecipeLoader {

    public static void run() {
        if (Mods.Dreamcraft.isLoaded()) return;
        loadCompressedBlockRecipes();
    }

    private static void loadCompressedBlockRecipes() {

        ModBlocks[] blocks = { ModBlocks.COMPRESSED_COBBLESTONE, ModBlocks.COMPRESSED_DIRT, ModBlocks.COMPRESSED_GRAVEL,
            ModBlocks.COMPRESSED_SAND, };

        for (ModBlocks modBlock : blocks) {
            if (!(modBlock.get() instanceof BlockCompressed block) || !modBlock.isEnabled()) return;

            GameRegistry
                .addRecipe(new ItemStack(block, 1, 0), "###", "###", "###", '#', new ItemStack(block.getBase(), 9, 0));

            GameRegistry.addShapelessRecipe(new ItemStack(block.getBase(), 9, 0), new ItemStack(block, 1, 0));

            for (int i = 0; i < 7; i++) {
                GameRegistry
                    .addRecipe(new ItemStack(block, 1, i + 1), "###", "###", "###", '#', new ItemStack(block, 1, i));

                GameRegistry.addShapelessRecipe(new ItemStack(block, 9, i), new ItemStack(block, 1, i + 1));
            }
        }
    }
}
