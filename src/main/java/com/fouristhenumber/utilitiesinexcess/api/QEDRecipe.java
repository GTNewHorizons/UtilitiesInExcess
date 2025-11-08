package com.fouristhenumber.utilitiesinexcess.api;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class QEDRecipe {

    private final Object[] inputs;
    private final ItemStack output;

    public QEDRecipe(Object[] inputs, ItemStack output) {
        this.inputs = inputs;
        this.output = output;
    }

    public ItemStack getOutput() {
        return this.output;
    }

    protected Object[] getInputs() {
        return inputs;
    }

    public boolean matches(InventoryCrafting inv) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Object rawRecipeInput = this.inputs[row + col * 3];
                ItemStack providedInput = inv.getStackInRowAndColumn(row, col);

                if (rawRecipeInput == null && providedInput == null) continue;
                if (rawRecipeInput == null || providedInput == null) return false;

                if (rawRecipeInput instanceof ItemStack recipeInput) {
                    if (recipeInput.getItem() != providedInput.getItem()) {
                        return false;
                    }

                    if (recipeInput.getItemDamage() != providedInput.getItemDamage()) {
                        return recipeInput.getItemDamage() == OreDictionary.WILDCARD_VALUE;
                    }
                } else if (rawRecipeInput instanceof ItemStack[]ores) {
                    boolean matched = false;
                    for (ItemStack ore : ores) {
                        if (OreDictionary.itemMatches(ore, providedInput, false)) {
                            matched = true;
                            break;
                        }
                    }

                    if (!matched) {
                        return false;
                    }
                }

            }
        }

        return true;
    }

    public ItemStack getResult() {
        return this.getOutput()
            .copy();
    }
}
