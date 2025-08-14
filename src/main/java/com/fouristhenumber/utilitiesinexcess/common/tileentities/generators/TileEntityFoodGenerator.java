package com.fouristhenumber.utilitiesinexcess.common.tileentities.generators;

import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.foodGeneratorBurnTimeMultiplier;
import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.foodGeneratorRFCapacity;
import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.foodGeneratorRFMultiplier;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

public class TileEntityFoodGenerator extends TileEntityBaseGeneratorWithItemFuel {

    public TileEntityFoodGenerator() {
        super(foodGeneratorRFCapacity);
    }

    @Override
    protected int getRFPerTick(ItemStack stack) {
        if (stack == null) return 0;
        if (!(stack.getItem() instanceof ItemFood foodItem)) return 0;
        // Obfuscated method is getHungerValue
        return foodItem.func_150905_g(stack) * foodGeneratorRFMultiplier;
    }

    @Override
    protected int getFuelBurnTime(ItemStack stack) {
        if (stack == null) return 0;
        if (!(stack.getItem() instanceof ItemFood foodItem)) return 0;
        // Obfuscated method is getSaturation
        return (int) (foodItem.func_150906_h(stack) * foodGeneratorBurnTimeMultiplier);
    }

    @Override
    public String getInventoryName() {
        return "tile.food_generator.name";
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (stack == null) return false;
        return stack.getItem() instanceof ItemFood;
    }
}
