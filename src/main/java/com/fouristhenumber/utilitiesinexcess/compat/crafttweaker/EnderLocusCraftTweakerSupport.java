package com.fouristhenumber.utilitiesinexcess.compat.crafttweaker;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.api.EnderLocusRecipe;
import com.fouristhenumber.utilitiesinexcess.api.EnderLocusRegistry;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.OneWayAction;
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
        MineTweakerAPI.apply(new IUndoableAction() {

            private boolean successful = false;

            @Override
            public void apply() {
                Object[] inputArray = new Object[9];

                for (int row = 0; row < 3; row++) {
                    for (int col = 0; col < 3; col++) {
                        IIngredient input = inputs[row][col];
                        if (input == null) {
                            inputArray[row * 3 + col] = null;
                            continue;
                        }

                        Object rawInput = input.getInternal();
                        if (rawInput instanceof ItemStack stack) {
                            inputArray[row * 3 + col] = stack;
                        } else if (rawInput instanceof String ore) {
                            inputArray[row * 3 + col] = OreDictionary.getOres(ore)
                                .toArray(new ItemStack[0]);
                        }
                    }
                }

                EnderLocusRegistry.instance()
                    .addRecipe(new EnderLocusRecipe(inputArray, MineTweakerMC.getItemStack(output)));
                successful = true;
            }

            @Override
            public boolean canUndo() {
                return successful;
            }

            @Override
            public void undo() {
                EnderLocusRegistry.instance()
                    .removeRecipe(MineTweakerMC.getItemStack(output));
            }

            @Override
            public String describe() {
                return "Adding Ender Locus recipe for " + StatCollector.translateToLocal(output.getName());
            }

            @Override
            public String describeUndo() {
                return "Undoing Ender Locus recipe addition for " + StatCollector.translateToLocal(output.getName());
            }

            @Override
            public Object getOverrideKey() {
                return null;
            }
        });
    }

    @ZenMethod
    public static void removeRecipe(IItemStack output) {
        MineTweakerAPI.apply(new OneWayAction() {

            @Override
            public void apply() {
                EnderLocusRegistry.instance()
                    .removeRecipe(MineTweakerMC.getItemStack(output));
            }

            @Override
            public String describe() {
                return "Removing Ender Locus recipe for " + StatCollector.translateToLocal(output.getName());
            }

            @Override
            public Object getOverrideKey() {
                return null;
            }
        });
    }
}
