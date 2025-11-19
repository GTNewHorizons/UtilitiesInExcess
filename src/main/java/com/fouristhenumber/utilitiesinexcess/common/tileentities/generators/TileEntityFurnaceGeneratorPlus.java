package com.fouristhenumber.utilitiesinexcess.common.tileentities.generators;

import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.*;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class TileEntityFurnaceGeneratorPlus extends TileEntityFurnaceGenerator {

    public TileEntityFurnaceGeneratorPlus() {
        super();
    }

    @Override
    protected int getRFPerTick(ItemStack currentBurningItem) {
        return furnaceGeneratorRFPerTick * 8;
    }

    @Override
    protected int getFuelBurnTime(ItemStack stack) {
        return (int) (TileEntityFurnace.getItemBurnTime(stack) * furnaceGeneratorFuelUsageRatio / 8);
    }

    @Override
    public String getInventoryName() {
        return "tile.furnace_generator_plus.name";
    }
}
