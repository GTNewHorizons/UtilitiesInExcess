package com.fouristhenumber.utilitiesinexcess.common.tileentities.generators;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TileEntityNetherStarGenerator extends TileEntityBaseGeneratorWithItemFuel {

    public TileEntityNetherStarGenerator() {
        super(1_000_000);
    }

    @Override
    protected int getRFPerTick(ItemStack currentBurningItem) {
        return 40_000;
    }

    @Override
    protected int getFuelBurnTime(ItemStack stack) {
        if (stack == null) return 0;
        Item item = stack.getItem();
        if (item == Items.nether_star) return 2400;
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
