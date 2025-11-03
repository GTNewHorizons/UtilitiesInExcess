package com.fouristhenumber.utilitiesinexcess.common.tileentities.generators;

import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.furnaceGeneratorFuelUsageRatio;
import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.furnaceGeneratorRFCapacity;
import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.furnaceGeneratorRFPerTick;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class TileEntityFurnaceGenerator extends TileEntityBaseGeneratorWithItemFuel {

    public TileEntityFurnaceGenerator() {
        super(furnaceGeneratorRFCapacity);
    }

    @Override
    protected int getRFPerTick(ItemStack currentBurningItem) {
        return furnaceGeneratorRFPerTick;
    }

    @Override
    protected int getFuelBurnTime(ItemStack stack) {
        return (int) (TileEntityFurnace.getItemBurnTime(stack) * furnaceGeneratorFuelUsageRatio);
    }

    @Override
    public String getInventoryName() {
        return "tile.furnace_generator.name";
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return TileEntityFurnace.isItemFuel(stack);
    }
}
