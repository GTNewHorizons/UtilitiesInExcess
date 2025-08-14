package com.fouristhenumber.utilitiesinexcess.common.tileentities.generators;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class TileEntityHighTemperatureFurnaceGenerator extends TileEntityBaseGeneratorWithItemFuel {

    public TileEntityHighTemperatureFurnaceGenerator() {
        super(500000, 1000);
    }

    @Override
    protected int getRFPerTick(ItemStack currentBurningItem) {
        return 200;
    }

    @Override
    protected int getFuelBurnTime(ItemStack stack) {
        return TileEntityFurnace.getItemBurnTime(stack) / 10;
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
