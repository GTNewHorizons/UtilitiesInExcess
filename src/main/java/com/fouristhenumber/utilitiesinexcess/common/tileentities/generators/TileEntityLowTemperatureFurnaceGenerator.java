package com.fouristhenumber.utilitiesinexcess.common.tileentities.generators;

import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.lowTemperatureFurnaceGeneratorFuelUsageRatio;
import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.lowTemperatureFurnaceGeneratorRFCapacity;
import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.lowTemperatureFurnaceGeneratorRFPerTick;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class TileEntityLowTemperatureFurnaceGenerator extends TileEntityBaseGeneratorWithItemFuel {

    public TileEntityLowTemperatureFurnaceGenerator() {
        super(lowTemperatureFurnaceGeneratorRFCapacity);
    }

    @Override
    protected int getRFPerTick(ItemStack currentBurningItem) {
        return lowTemperatureFurnaceGeneratorRFPerTick;
    }

    @Override
    protected int getFuelBurnTime(ItemStack stack) {
        return (int) (TileEntityFurnace.getItemBurnTime(stack) * lowTemperatureFurnaceGeneratorFuelUsageRatio);
    }

    @Override
    public String getInventoryName() {
        return "tile.low_temperature_furnace_generator.name";
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return TileEntityFurnace.isItemFuel(stack);
    }
}
