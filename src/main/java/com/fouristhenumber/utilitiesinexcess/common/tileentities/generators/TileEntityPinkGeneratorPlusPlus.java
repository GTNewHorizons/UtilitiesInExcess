package com.fouristhenumber.utilitiesinexcess.common.tileentities.generators;

import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.pinkGeneratorFuelBurnTime;
import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.pinkGeneratorRFPerTick;

import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.utils.PinkFuelHelper;

public class TileEntityPinkGeneratorPlusPlus extends TileEntityPinkGenerator {

    @Override
    protected int getRFPerTick(ItemStack currentBurningItem) {
        return pinkGeneratorRFPerTick * 64;
    }

    @Override
    protected int getFuelBurnTime(ItemStack stack) {
        if (stack == null) return 0;
        if (PinkFuelHelper.pinkFuelItems
            .contains(new PinkFuelHelper.ItemMetaPair(stack.getItem(), stack.getItemDamage()))) {
            return pinkGeneratorFuelBurnTime / 64;
        }
        return 0;
    }
}
