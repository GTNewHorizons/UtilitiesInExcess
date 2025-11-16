package com.fouristhenumber.utilitiesinexcess.common.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;

public class RecipeInvertedIngotStable extends ShapedRecipes {

    public RecipeInvertedIngotStable(int width, int height, ItemStack[] input, ItemStack output) {
        super(width, height, input, output);
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        // Reject if not a vanilla workbench
        if (inv.eventHandler == null
            || inv.eventHandler.getClass() != net.minecraft.inventory.ContainerWorkbench.class) {
            return false;
        }

        return super.matches(inv, world);
    }
}
