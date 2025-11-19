package com.fouristhenumber.utilitiesinexcess.common.tileentities.generators;

import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.*;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class TileEntityFurnaceGeneratorPlusPlus extends TileEntityFurnaceGenerator {

    public TileEntityFurnaceGeneratorPlusPlus() {
        super();
    }

    @Override
    protected int getRFPerTick(ItemStack currentBurningItem) {
        return furnaceGeneratorRFPerTick * 64;
    }

    @Override
    protected int getFuelBurnTime(ItemStack stack) {
        return (int) (TileEntityFurnace.getItemBurnTime(stack) * furnaceGeneratorFuelUsageRatio / 64);
    }

    @Override
    public String getInventoryName() {
        return "tile.furnace_generator_plusplus.name";
    }
}
