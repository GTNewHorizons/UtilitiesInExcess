package com.fouristhenumber.utilitiesinexcess.common.tileentities.generators;

import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.netherStarGeneratorFuelBurnTime;
import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.netherStarGeneratorRFCapacity;
import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.netherStarGeneratorRFPerTick;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TileEntityNetherStarGenerator extends TileEntityBaseGeneratorWithItemFuel {

    public TileEntityNetherStarGenerator() {
        super(netherStarGeneratorRFCapacity);
    }

    @Override
    protected int getRFPerTick(ItemStack currentBurningItem) {
        return netherStarGeneratorRFPerTick;
    }

    @Override
    protected int getFuelBurnTime(ItemStack stack) {
        if (stack == null) return 0;
        Item item = stack.getItem();
        if (item == Items.nether_star) return netherStarGeneratorFuelBurnTime;
        return 0;
    }

    @Override
    public String getInventoryName() {
        return "tile.nether_star_generator.name";
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (stack == null) return false;
        return stack.getItem() == Items.nether_star;
    }
}
