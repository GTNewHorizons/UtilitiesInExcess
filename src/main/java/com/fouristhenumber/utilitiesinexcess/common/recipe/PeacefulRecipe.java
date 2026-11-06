package com.fouristhenumber.utilitiesinexcess.common.recipe;

import java.util.ArrayList;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class PeacefulRecipe implements IRecipe {

    // If we ever need to include shapeless peaceful recipes, just change all ShapedRecipe to IRecipe in this class,
    // then create a shapeless peaceful recipe handler & add filtering logic to both handlers.

    public static final ArrayList<ShapedRecipes> shapedPeacefulRecipes = new ArrayList<>();

    // We cannot just extend ShapedRecipes because then the default NEI Crafting Recipe handler slurps up these recipes.
    private final ShapedRecipes delegate;

    public PeacefulRecipe(ShapedRecipes recipe) {
        this.delegate = recipe;
        shapedPeacefulRecipes.add(delegate);
    }

    @Override
    public ItemStack getRecipeOutput() {
        return delegate.getRecipeOutput();
    }

    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        return world != null && world.difficultySetting == EnumDifficulty.PEACEFUL
            && delegate.matches(inventory, world);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        return delegate.getCraftingResult(inventory);
    }

    @Override
    public int getRecipeSize() {
        return delegate.getRecipeSize();
    }
}
