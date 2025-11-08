package com.fouristhenumber.utilitiesinexcess.compat.nei;

import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.api.QEDRecipe;
import com.fouristhenumber.utilitiesinexcess.api.QEDRegistry;

import codechicken.nei.NEIClientConfig;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.recipe.ShapedRecipeHandler;

public class QEDRecipeHandler extends ShapedRecipeHandler {

    @Override
    public String getOverlayIdentifier() {
        return "qed_recipes";
    }

    @Override
    public String getRecipeName() {
        return "QED Recipes";
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (QEDRecipe qedRecipe : QEDRegistry.instance()
            .getAllRecipes()) {
            if (!qedRecipe.containsInput(ingredient)) continue;
            CachedShapedRecipe neiRecipe = makeNeiRecipe(qedRecipe);
            if (neiRecipe == null) continue;
            neiRecipe.computeVisuals();
            neiRecipe.setIngredientPermutation(neiRecipe.ingredients, ingredient);
            this.arecipes.add(neiRecipe);
        }
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(getOverlayIdentifier())) {
            for (QEDRecipe qedRecipe : QEDRegistry.instance()
                .getAllRecipes()) {
                CachedShapedRecipe neiRecipe = makeNeiRecipe(qedRecipe);
                if (neiRecipe == null) continue;
                neiRecipe.computeVisuals();
                this.arecipes.add(neiRecipe);
            }
        } else if (outputId.equals("item")) {
            loadCraftingRecipes((ItemStack) results[0]);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (QEDRecipe qedRecipe : QEDRegistry.instance()
            .getAllRecipes()) {
            if (NEIServerUtils.areStacksSameTypeCrafting(qedRecipe.getOutput(), result)) {
                CachedShapedRecipe neiRecipe = makeNeiRecipe(qedRecipe);
                if (neiRecipe == null) continue;
                neiRecipe.computeVisuals();
                this.arecipes.add(neiRecipe);
            }
        }
    }

    private CachedShapedRecipe makeNeiRecipe(QEDRecipe recipe) {
        try {
            Object[] inputs = recipe.getInputs();
            for (Object rawInput : inputs) {
                if (rawInput instanceof ItemStack[]inputOptions && inputOptions.length == 0) {
                    return null;
                }
            }

            return new CachedShapedRecipe(3, 3, inputs, recipe.getOutput());
        } catch (Exception e) {
            NEIClientConfig.logger.error("Error loading QED recipe: ", e);
            return null;
        }
    }
}
