package com.fouristhenumber.utilitiesinexcess.common.recipe;

import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockCompressed;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;

public class RecipeLoader {

    public static void run() {
        if (Mods.Dreamcraft.isLoaded()) return;
        loadCompressedBlockRecipes();
    }

    private static boolean addShapedRecipe(Object outputObject, Object... params) {
        return DisableableItemStack.addShapedRecipe(outputObject, params);
    }

    private static boolean addShapelessRecipe(Object outputObject, Object... params) {
        return DisableableItemStack.addShapelessRecipe(outputObject, params);
    }

    private static void loadCompressedBlockRecipes() {
        ModBlocks[] blocks = { ModBlocks.COMPRESSED_COBBLESTONE, ModBlocks.COMPRESSED_DIRT, ModBlocks.COMPRESSED_GRAVEL,
            ModBlocks.COMPRESSED_SAND, };

        for (ModBlocks modBlock : blocks) {
            if (!(modBlock.get() instanceof BlockCompressed block) || !modBlock.isEnabled()) continue;
            addShapedRecipe(
                new DisableableItemStack(modBlock),
                "###",
                "###",
                "###",
                '#',
                new ItemStack(block.getBase(), 9));
            addShapelessRecipe(new DisableableItemStack(modBlock, 9), new DisableableItemStack(modBlock, 1));
            for (int i = 0; i < 7; i++) {
                addShapedRecipe(
                    new DisableableItemStack(modBlock, 1, i + 1),
                    "###",
                    "###",
                    "###",
                    '#',
                    new DisableableItemStack(modBlock, 1, i));
                addShapelessRecipe(
                    new DisableableItemStack(modBlock, 9, i),
                    new DisableableItemStack(modBlock, 1, i + 1));
            }
        }
    }
}
