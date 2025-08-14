package com.fouristhenumber.utilitiesinexcess.common.tileentities.generators;

import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.pinkGeneratorFuelBurnTime;
import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.pinkGeneratorRFCapacity;
import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.pinkGeneratorRFPerTick;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.utils.PinkFuelHelper;

public class TileEntityPinkGenerator extends TileEntityBaseGeneratorWithItemFuel {

    public TileEntityPinkGenerator() {
        super(pinkGeneratorRFCapacity);
    }

    @Override
    protected int getRFPerTick(ItemStack currentBurningItem) {
        return pinkGeneratorRFPerTick;
    }

    @Override
    protected int getFuelBurnTime(ItemStack stack) {
        if (stack == null) return 0;
        Item item = stack.getItem();
        if (PinkFuelHelper.pinkFuelItems.contains(item)) {
            return pinkGeneratorFuelBurnTime;
        }
        return 0;
    }

    @Override
    public String getInventoryName() {
        return "tile.pink_generator.name";
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return stack != null && PinkFuelHelper.pinkFuelItems.contains(stack.getItem());
    }
}
