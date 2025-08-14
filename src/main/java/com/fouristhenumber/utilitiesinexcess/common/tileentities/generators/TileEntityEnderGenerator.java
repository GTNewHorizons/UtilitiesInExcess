package com.fouristhenumber.utilitiesinexcess.common.tileentities.generators;

import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.enderGeneratorRFCapacity;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TileEntityEnderGenerator extends TileEntityBaseGeneratorWithItemFuel {

    public TileEntityEnderGenerator() {
        super(enderGeneratorRFCapacity);
    }

    @Override
    protected int getRFPerTick(ItemStack currentBurningItem) {
        return 40;
    }

    // TODO: This should also allow ender-lily seeds when we add those
    @Override
    protected int getFuelBurnTime(ItemStack stack) {
        if (stack == null) return 0;
        Item item = stack.getItem();
        if (item == Items.ender_pearl) return 800;
        if (item == Items.ender_eye) return 3000;
        return 0;
    }

    @Override
    public String getInventoryName() {
        return "tile.ender_generator.name";
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (stack == null) return false;
        Item item = stack.getItem();
        return item == Items.ender_eye || item == Items.ender_pearl;
    }
}
