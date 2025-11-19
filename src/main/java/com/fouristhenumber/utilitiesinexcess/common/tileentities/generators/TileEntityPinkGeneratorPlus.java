package com.fouristhenumber.utilitiesinexcess.common.tileentities.generators;

import com.fouristhenumber.utilitiesinexcess.utils.PinkFuelHelper;
import net.minecraft.item.ItemStack;

import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.pinkGeneratorFuelBurnTime;
import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.pinkGeneratorRFPerTick;

public class TileEntityPinkGeneratorPlus extends TileEntityPinkGenerator{
    public TileEntityPinkGeneratorPlus() {super();}

    @Override
    protected int getRFPerTick(ItemStack currentBurningItem) {
        return pinkGeneratorRFPerTick*8;
    }

    @Override
    protected int getFuelBurnTime(ItemStack stack) {
        if (stack == null) return 0;
        if (PinkFuelHelper.pinkFuelItems
            .contains(new PinkFuelHelper.ItemMetaPair(stack.getItem(), stack.getItemDamage()))) {
            return pinkGeneratorFuelBurnTime/8;
        }
        return 0;
    }
}
