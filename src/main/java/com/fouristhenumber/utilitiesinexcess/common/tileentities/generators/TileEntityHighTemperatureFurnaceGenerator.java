package com.fouristhenumber.utilitiesinexcess.common.tileentities.generators;

import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.highTemperatureFurnaceGeneratorFuelUsageRatio;
import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.highTemperatureFurnaceGeneratorRFCapacity;
import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.highTemperatureFurnaceGeneratorRFPerTick;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class TileEntityHighTemperatureFurnaceGenerator extends TileEntityBaseGeneratorWithItemFuel {

    public TileEntityHighTemperatureFurnaceGenerator() {
        super(highTemperatureFurnaceGeneratorRFCapacity);
    }

    @Override
    protected int getRFPerTick(ItemStack currentBurningItem) {
        return highTemperatureFurnaceGeneratorRFPerTick;
    }

    @Override
    protected int getFuelBurnTime(ItemStack stack) {
        return (int) (TileEntityFurnace.getItemBurnTime(stack) * highTemperatureFurnaceGeneratorFuelUsageRatio);
    }

    @Override
    public String getInventoryName() {
        return "tile.high_temperature_furnace_generator.name";
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return TileEntityFurnace.isItemFuel(stack);
    }
}
