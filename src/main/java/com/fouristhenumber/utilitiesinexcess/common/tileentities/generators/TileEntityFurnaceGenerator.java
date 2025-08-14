package com.fouristhenumber.utilitiesinexcess.common.tileentities.generators;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class TileEntityFurnaceGenerator extends TileEntityBaseGeneratorWithItemFuel {

    public TileEntityFurnaceGenerator() {
        super(100000);
    }

    @Override
    protected int getRFPerTick(ItemStack currentBurningItem) {
        return 40;
    }

    @Override
    protected int getFuelBurnTime(ItemStack stack) {
        return TileEntityFurnace.getItemBurnTime(stack);
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
