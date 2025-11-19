package com.fouristhenumber.utilitiesinexcess.common.tileentities.generators;

import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.*;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

public class TileEntityFoodGeneratorPlus extends TileEntityFoodGenerator {

    public TileEntityFoodGeneratorPlus() {
        super();
    }

    @Override
    protected int getRFPerTick(ItemStack stack) {
        if (stack == null) return 0;
        if (!(stack.getItem() instanceof ItemFood foodItem)) return 0;
        // Obfuscated method is getHungerValue
        return foodItem.func_150905_g(stack) * foodGeneratorRFMultiplier * 8;
    }

    @Override
    protected int getFuelBurnTime(ItemStack stack) {
        if (stack == null) return 0;
        if (!(stack.getItem() instanceof ItemFood foodItem)) return 0;
        // Obfuscated method is getSaturation
        return (int) (foodItem.func_150906_h(stack) * foodGeneratorBurnTimeMultiplier / 8);
    }

    @Override
    public String getInventoryName() {
        return "tile.food_generator_plus.name";
    }
}
