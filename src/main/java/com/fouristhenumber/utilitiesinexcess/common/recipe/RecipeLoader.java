package com.fouristhenumber.utilitiesinexcess.common.recipe;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockCompressed;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;

public class RecipeLoader {

    public static void run() {
        if (Mods.Dreamcraft.isLoaded()) return;
        loadCompressedBlockRecipes();
        loadInversionRecipes();
    }

    private static void loadInversionRecipes() {
        boolean diamondStickEnabled = ModItems.DIAMOND_STICK.isEnabled();
        boolean invertedIngotEnabled = ModItems.INVERTED_INGOT.isEnabled();
        if (diamondStickEnabled) {
            GameRegistry.addRecipe(new ItemStack(ModItems.DIAMOND_STICK.get(), 4), "#", "#", '#', Items.diamond);
        }
        if (invertedIngotEnabled && ModItems.INVERSION_SIGIL_ACTIVE.isEnabled()) {
            GameRegistry.addRecipe(
                new ItemStack(ModItems.INVERTED_INGOT.get(), 1),
                "i",
                "#",
                "d",
                'i',
                Items.iron_ingot,
                '#',
                ModItems.INVERSION_SIGIL_ACTIVE.get(),
                'd',
                Items.diamond);
        }
        if (diamondStickEnabled && invertedIngotEnabled && ModItems.GLUTTONS_AXE.isEnabled()) {
            GameRegistry.addRecipe(
                new ItemStack(ModItems.GLUTTONS_AXE.get(), 1),
                "ii",
                "is",
                " s",
                'i',
                ModItems.INVERTED_INGOT.get(),
                's',
                ModItems.DIAMOND_STICK.get());
        }
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
