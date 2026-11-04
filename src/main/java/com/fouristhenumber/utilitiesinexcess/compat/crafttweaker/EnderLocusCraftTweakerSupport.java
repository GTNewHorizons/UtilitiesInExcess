package com.fouristhenumber.utilitiesinexcess.compat.crafttweaker;

import java.util.List;
import java.util.Objects;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.api.EnderLocusRecipe;
import com.fouristhenumber.utilitiesinexcess.api.EnderLocusRegistry;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.annotations.ModOnly;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.utilitiesinexcess.EnderLocus")
@ModOnly(UtilitiesInExcess.MODID)
public class EnderLocusCraftTweakerSupport {

    @ZenMethod
    public static void addRecipe(IItemStack output, IIngredient[][] inputs) {
        final EnderLocusRecipe recipe = parseRecipe(output, inputs);
        if (recipe == null) return;

        MineTweakerAPI.apply(new ActionAddLocusRecipe(recipe));
    }

    @ZenMethod
    public static void removeRecipes(IItemStack output) {
        MineTweakerAPI.apply(new ActionRemoveLocusRecipes(output));
    }

    private static @Nullable EnderLocusRecipe parseRecipe(IItemStack output, IIngredient[][] inputs) {
        Object[] inputArray = new Object[9];
        boolean hasInput = false;

        if (inputs.length != 3 || inputs[0].length != 3 || inputs[1].length != 3 || inputs[2].length != 3) {
            MineTweakerAPI.logError("Ender Locus recipe must be provided a 3x3 grid of input elements");
            return null;
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                IIngredient ingredient = inputs[row][col];
                if (ingredient == null) continue;

                List<IItemStack> stacks = ingredient.getItems();
                if (stacks == null || stacks.size() == 0) {
                    MineTweakerAPI.logError("Unsupported ingredient for Ender Locus recipe: " + ingredient);
                    return null;
                }

                // Note: getItemStack can only fail if other mods add their own implementation of IItemStack.
                if (stacks.size() == 1) {
                    ItemStack input = MineTweakerMC.getItemStack(stacks.get(0));
                    if (input == null) return null;

                    inputArray[row * 3 + col] = input;
                } else {
                    ItemStack[] input = stacks.stream()
                        .map(MineTweakerMC::getItemStack)
                        .filter(Objects::nonNull)
                        .toArray(ItemStack[]::new);

                    if (input.length == 0) return null;

                    inputArray[row * 3 + col] = input;
                }

                hasInput = true;
            }
        }

        if (!hasInput) {
            MineTweakerAPI.logError("Ender Locus recipe for " + output + " must have at least one ingredient.");
            return null;
        }

        final ItemStack outStack = MineTweakerMC.getItemStack(output);
        if (outStack == null) return null;

        return new EnderLocusRecipe(inputArray, outStack);
    }

    private static class ActionAddLocusRecipe implements IUndoableAction {

        private final EnderLocusRecipe recipe;

        public ActionAddLocusRecipe(EnderLocusRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void apply() {
            EnderLocusRegistry.instance()
                .addRecipe(recipe);
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            EnderLocusRegistry.instance()
                .removeRecipe(recipe);
        }

        @Override
        public String describe() {
            return "Adding Ender Locus recipe for " + recipe.getOutput()
                .getDisplayName();
        }

        @Override
        public String describeUndo() {
            return "Undoing Ender Locus recipe addition for " + recipe.getOutput()
                .getDisplayName();
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

    private static class ActionRemoveLocusRecipes implements IUndoableAction {

        private EnderLocusRecipe[] removed;
        private final IItemStack filter;

        private ActionRemoveLocusRecipes(IItemStack filter) {
            this.filter = filter;
        }

        @Override
        public void apply() {
            this.removed = EnderLocusRegistry.instance()
                .removeRecipes(recipe -> {
                    try {
                        return filter.matchesExact(MineTweakerMC.getIItemStack(recipe.getOutput()));
                    } catch (Exception e) {
                        MineTweakerAPI.logError("Error while filtering Ender Locus recipes: ", e);
                        return false;
                    }
                })
                .toArray(new EnderLocusRecipe[0]);
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            for (EnderLocusRecipe recipe : this.removed) {
                EnderLocusRegistry.instance()
                    .addRecipe(recipe);
            }
        }

        @Override
        public String describe() {
            return "Removing Ender Locus recipes which output " + this.filter;
        }

        @Override
        public String describeUndo() {
            return "Undoing removal of " + this.removed.length + " Ender Locus recipe(s) which output " + this.filter;
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}
