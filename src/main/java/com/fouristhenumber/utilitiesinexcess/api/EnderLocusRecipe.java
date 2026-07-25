package com.fouristhenumber.utilitiesinexcess.api;

import static net.minecraft.item.ItemStack.areItemStacksEqual;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.gtnewhorizon.gtnhlib.util.ItemUtil;

public class EnderLocusRecipe {

    private final Object[] inputs;
    private final ItemStack output;

    public EnderLocusRecipe(Object[] inputs, ItemStack output) {
        this.inputs = inputs;
        this.output = output;
    }

    public ItemStack getOutput() {
        return this.output;
    }

    public Object[] getInputs() {
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
                    if (!ItemUtil.areStacksEqual(recipeInput, providedInput)) return false;
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

    public boolean containsInput(ItemStack testStack) {
        for (Object rawInput : this.inputs) {
            if (rawInput instanceof ItemStack inputStack) {
                if (areItemStacksEqual(inputStack, testStack)) {
                    return true;
                }
            } else if (rawInput instanceof ItemStack[]inputStacks) {
                for (ItemStack inputStack : inputStacks) {
                    if (areItemStacksEqual(inputStack, testStack)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public ItemStack getResult() {
        return this.getOutput()
            .copy();
    }
}
