package com.fouristhenumber.utilitiesinexcess.api;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.recipe.DisableableItemStack;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;

public final class QEDRegistry {

    private static final QEDRegistry REGISTRY = new QEDRegistry();

    private final ObjectList<QEDRecipe> recipes = new ObjectArrayList<>();

    private QEDRegistry() {}

    public static QEDRegistry instance() {
        return REGISTRY;
    }

    public void addRecipe(ItemStack output, String[] pattern, Object... inputs) {
        verifyPattern(pattern);

        // Parse the input mapping
        Char2ObjectMap<Object> mapping = new Char2ObjectOpenHashMap<>();
        for (int i = 0; i < inputs.length; i += 2) {
            char symbol = (char) inputs[i];
            Object rawInput = inputs[i + 1];

            if (rawInput instanceof Item item) {
                mapping.put(symbol, new ItemStack(item));
            } else if (rawInput instanceof Block block) {
                mapping.put(symbol, new ItemStack(block, 1, OreDictionary.WILDCARD_VALUE));
            } else if (rawInput instanceof ItemStack stack) {
                mapping.put(symbol, stack.copy());
            } else if (rawInput instanceof DisableableItemStack stack) {
                mapping.put(symbol, stack.theStack);
            } else if (rawInput instanceof ModItems item) {
                mapping.put(symbol, item.newItemStack());
            } else if (rawInput instanceof ModBlocks block) {
                mapping.put(symbol, block.newItemStack());
            } else if (rawInput instanceof String ore) {
                mapping.put(
                    symbol,
                    OreDictionary.getOres(ore)
                        .toArray(new ItemStack[0]));
            }
        }

        // Build the input array, a 3x3 going left->right then top->bottom
        Object[] inputArray = new Object[9];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                char symbol = pattern[row].charAt(col);

                if (mapping.containsKey(symbol)) {
                    inputArray[row * 3 + col] = mapping.get(symbol);
                } else {
                    inputArray[row * 3 + col] = null;
                }
            }
        }

        QEDRecipe qedRecipe = new QEDRecipe(inputArray, output);
        this.recipes.add(qedRecipe);
        logRecipe(qedRecipe);
    }

    public void addRecipe(QEDRecipe recipe) {
        this.recipes.add(recipe);
        logRecipe(recipe);
    }

    private void verifyPattern(String[] pattern) {
        if (pattern == null || pattern.length != 3
            || pattern[0].length() != 3
            || pattern[1].length() != 3
            || pattern[2].length() != 3) {
            throw new IllegalArgumentException("QED recipe pattern must be 3 strings each with 3 characters");
        }
    }

    public void removeRecipe(ItemStack output) {
        this.recipes.removeIf(recipe -> ItemStack.areItemStacksEqual(recipe.getOutput(), output));
    }

    public ItemStack findRecipe(InventoryCrafting inv, boolean consume) {
        for (QEDRecipe recipe : this.recipes) {
            if (recipe.matches(inv)) {
                if (consume) {
                    for (int i = 0; i < 9; i++) {
                        inv.decrStackSize(i, 1);
                    }
                }
                return recipe.getResult();
            }
        }
        return null;
    }

    public List<QEDRecipe> getAllRecipes() {
        return ObjectLists.unmodifiable(this.recipes);
    }

    private static void logRecipe(QEDRecipe recipe) {
        Object[] inputs = recipe.getInputs();
        String[] logOutput = new String[9];
        for (int i = 0; i < 9; i++) {
            Object input = inputs[i];
            if (input instanceof ItemStack stack) {
                logOutput[i] = stack.getDisplayName();
            } else {
                logOutput[i] = findOreForStacks((ItemStack[]) input);
            }
        }

        UtilitiesInExcess.LOG.info("======================================================");
        UtilitiesInExcess.LOG.info(
            "Adding QED Recipe for: {}",
            recipe.getOutput()
                .getDisplayName());
        UtilitiesInExcess.LOG.info("Pattern:");
        UtilitiesInExcess.LOG.info(String.format("[%16s, %16s, %16s]", logOutput[0], logOutput[1], logOutput[2]));
        UtilitiesInExcess.LOG.info(String.format("[%16s, %16s, %16s]", logOutput[3], logOutput[4], logOutput[5]));
        UtilitiesInExcess.LOG.info(String.format("[%16s, %16s, %16s]", logOutput[6], logOutput[7], logOutput[8]));
        UtilitiesInExcess.LOG.info("======================================================");
    }

    private static String findOreForStacks(ItemStack[] stacks) {
        try {
            return Arrays.stream(stacks)
                .map(
                    stack -> Arrays.stream(OreDictionary.getOreIDs(stack))
                        .boxed()
                        .collect(Collectors.toSet()))
                .reduce((s1, s2) -> {
                    s1.retainAll(s2);
                    return s1;
                })
                .map(
                    s -> s.stream()
                        .mapToInt(Integer::intValue)
                        .toArray())
                .filter(a -> a.length > 0)
                .map(a -> OreDictionary.getOreName(a[0]))
                .orElse("idk wtf this ore is");
        } catch (Exception e) {
            return "idk wtf this ore is";
        }
    }
}
