package com.fouristhenumber.utilitiesinexcess.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class PinkFuelHelper {

    public static final Set<Item> pinkFuelItems = new HashSet<>();

    public static void scanRecipesForPinkFuel() {
        ItemStack pinkDye = new ItemStack(Items.dye, 1, 9);
        ItemStack pinkWool = new ItemStack(Blocks.wool, 1, 6);

        List<IRecipe> recipes = CraftingManager.getInstance()
            .getRecipeList();
        for (Object obj : recipes) {
            if (obj instanceof IRecipe recipe) {
                for (ItemStack input : getAllRecipeInputs(recipe)) {
                    if (input.isItemEqual(pinkDye) || input.isItemEqual(pinkWool)) {
                        ItemStack output = recipe.getRecipeOutput();
                        if (output != null) {
                            pinkFuelItems.add(output.getItem());
                        }
                        break;
                    }
                }
            }
        }

        pinkFuelItems.add(pinkDye.getItem());
        pinkFuelItems.add(pinkWool.getItem());
    }

    private static List<ItemStack> getAllRecipeInputs(IRecipe recipe) {
        List<ItemStack> stacks = new ArrayList<>();
        if (recipe instanceof ShapedRecipes) {
            Collections.addAll(stacks, ((ShapedRecipes) recipe).recipeItems);
        } else if (recipe instanceof ShapelessRecipes) {
            stacks.addAll(((ShapelessRecipes) recipe).recipeItems);
        } else if (recipe instanceof ShapedOreRecipe) {
            Object[] inputs = ((ShapedOreRecipe) recipe).getInput();
            for (Object o : inputs) stacks.addAll(oreDictExpand(o));
        } else if (recipe instanceof ShapelessOreRecipe) {
            List<Object> inputs = ((ShapelessOreRecipe) recipe).getInput();
            for (Object o : inputs) stacks.addAll(oreDictExpand(o));
        }
        return stacks;
    }

    // This will allow recipes with oredict dye to work
    private static List<ItemStack> oreDictExpand(Object o) {
        List<ItemStack> stacks = new ArrayList<>();
        if (o instanceof ItemStack) stacks.add((ItemStack) o);
        else if (o instanceof List) stacks.addAll((List<ItemStack>) o);
        return stacks;
    }
}
