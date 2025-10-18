package com.fouristhenumber.utilitiesinexcess.common.tileentities.generators;

import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.TNTGeneratorExplosions;
import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.TNTGeneratorGunpowderFuelValue;
import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.TNTGeneratorRFCapacity;
import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.TNTGeneratorTNTFuelValue;
import static com.fouristhenumber.utilitiesinexcess.utils.UIEUtils.uieRandom;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TileEntityTNTGenerator extends TileEntityBaseGeneratorWithItemFuel {

    public TileEntityTNTGenerator() {
        super(TNTGeneratorRFCapacity);
    }

    @Override
    protected int getRFPerTick(ItemStack stack) {
        if (stack == null) return 0;
        Item item = stack.getItem();
        if (item == Item.getItemFromBlock(Blocks.tnt)) return TNTGeneratorTNTFuelValue;
        if (item == Items.gunpowder) return TNTGeneratorGunpowderFuelValue;
        return 0;
    }

    @Override
    protected int getFuelBurnTime(ItemStack stack) {
        return 1;
    }

    @Override
    public String getInventoryName() {
        return "tile.tnt_generator.name";
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (stack == null) return false;
        Item item = stack.getItem();
        return item == Item.getItemFromBlock(Blocks.tnt) || item == Items.gunpowder;
    }

    @Override
    protected void onBurnTick() {
        if (TNTGeneratorExplosions) {
            worldObj.newExplosion(
                null,
                xCoord + (10 - uieRandom.nextInt(20)) + 0.5,
                yCoord + 0.5,
                zCoord + (10 - uieRandom.nextInt(20)) + 0.5,
                4.0F,
                false,
                false);
        }
    }
}
