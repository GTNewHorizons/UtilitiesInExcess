package com.fouristhenumber.utilitiesinexcess.compat.nei;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.StatCollector;
import net.minecraft.world.EnumDifficulty;

import com.fouristhenumber.utilitiesinexcess.common.recipe.PeacefulRecipe;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.recipe.GuiOverlayButton;
import codechicken.nei.recipe.IRecipeHandler;
import codechicken.nei.recipe.ShapedRecipeHandler;

public class ShapedPeacefulRecipeHandler extends ShapedRecipeHandler {

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("uie.nei.title.shaped_peaceful_crafting");
    }

    @Override
    public void loadUsageRecipes(String inputId, Object... ingredients) {
        if (worldIsViolent()) return;

        super.loadUsageRecipes(inputId, ingredients);
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (ShapedRecipes recipe : PeacefulRecipe.shapedPeacefulRecipes) {
            CachedShapedRecipe neiRecipe = new CachedShapedRecipe(recipe);

            if (neiRecipe.contains(neiRecipe.ingredients, ingredient)) {
                neiRecipe.computeVisuals();
                neiRecipe.setIngredientPermutation(neiRecipe.ingredients, ingredient);
                this.arecipes.add(neiRecipe);
            }
        }
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (worldIsViolent()) return;

        if (outputId.equals(this.getOverlayIdentifier()) || outputId.equals("crafting")) {
            for (ShapedRecipes recipe : PeacefulRecipe.shapedPeacefulRecipes) {
                CachedShapedRecipe neiRecipe = new CachedShapedRecipe(recipe);
                neiRecipe.computeVisuals();
                arecipes.add(neiRecipe);
            }
        } else if (outputId.equals("crafting2x2")) {
            for (ShapedRecipes recipe : PeacefulRecipe.shapedPeacefulRecipes) {
                if (recipe.recipeWidth <= 2 && recipe.recipeHeight <= 2) {
                    CachedShapedRecipe neiRecipe = new CachedShapedRecipe(recipe);
                    neiRecipe.computeVisuals();
                    arecipes.add(neiRecipe);
                }
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (ShapedRecipes recipe : PeacefulRecipe.shapedPeacefulRecipes) {
            if (NEIServerUtils.areStacksSameTypeCrafting(recipe.getRecipeOutput(), result)) {
                CachedShapedRecipe neiRecipe = new CachedShapedRecipe(recipe);
                neiRecipe.computeVisuals();
                this.arecipes.add(neiRecipe);
            }
        }
    }

    @Override
    public IOverlayHandler getOverlayHandler(GuiContainer gui, int recipe) {
        return new PeacefulOverlayHandler(super.getOverlayHandler(gui, recipe));
    }

    private static boolean worldIsViolent() {
        final var world = Minecraft.getMinecraft().theWorld;
        return world != null && world.difficultySetting != EnumDifficulty.PEACEFUL;
    }

    public static class PeacefulOverlayHandler implements IOverlayHandler {

        private final IOverlayHandler delegate;

        public PeacefulOverlayHandler(IOverlayHandler delegate) {
            this.delegate = delegate;
        }

        @Override
        public void overlayRecipe(GuiContainer firstGui, IRecipeHandler recipe, int recipeIndex, boolean maxTransfer) {
            if (!worldIsViolent()) delegate.overlayRecipe(firstGui, recipe, recipeIndex, maxTransfer);
        }

        @Override
        public int transferRecipe(GuiContainer firstGui, IRecipeHandler recipe, int recipeIndex, int multiplier) {
            return worldIsViolent() ? 0 : delegate.transferRecipe(firstGui, recipe, recipeIndex, multiplier);
        }

        @Override
        public boolean canFillCraftingGrid(GuiContainer firstGui, IRecipeHandler recipe, int recipeIndex) {
            return !worldIsViolent() && delegate.canFillCraftingGrid(firstGui, recipe, recipeIndex);
        }

        @Override
        public boolean craft(GuiContainer firstGui, IRecipeHandler recipe, int recipeIndex, int multiplier) {
            return !worldIsViolent() && delegate.craft(firstGui, recipe, recipeIndex, multiplier);
        }

        @Override
        public boolean canCraft(GuiContainer firstGui, IRecipeHandler handler, int recipeIndex) {
            return !worldIsViolent() && delegate.canCraft(firstGui, handler, recipeIndex);
        }

        @Override
        public boolean requireShiftForOverlayRecipe() {
            return delegate.requireShiftForOverlayRecipe();
        }

        @Override
        public List<GuiOverlayButton.ItemOverlayState> presenceOverlay(GuiContainer firstGui, IRecipeHandler recipe,
            int recipeIndex) {
            return worldIsViolent() ? Collections.emptyList() : delegate.presenceOverlay(firstGui, recipe, recipeIndex);
        }
    }
}
