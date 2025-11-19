package com.fouristhenumber.utilitiesinexcess.common.tileentities.generators;

import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.*;
import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.enderGeneratorEnderEyeBurnTime;
import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.enderGeneratorEnderLotusSeedBurnTime;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.ModItems;

public class TileEntityEnderGeneratorPlusPlus extends TileEntityEnderGenerator {

    public TileEntityEnderGeneratorPlusPlus() {
        super();
    }

    @Override
    protected int getRFPerTick(ItemStack currentBurningItem) {
        return enderGeneratorRFPerTick * 64;
    }

    @Override
    protected int getFuelBurnTime(ItemStack stack) {
        if (stack == null) return 0;
        Item item = stack.getItem();
        if (item == Items.ender_pearl) return enderGeneratorEnderPearlBurnTime / 64;
        if (item == Items.ender_eye) return enderGeneratorEnderEyeBurnTime / 64;
        if (ModItems.ENDER_LOTUS_SEED.isEnabled() && item == ModItems.ENDER_LOTUS_SEED.get())
            return enderGeneratorEnderLotusSeedBurnTime / 64;
        return 0;
    }

    @Override
    public String getInventoryName() {
        return "tile.ender_generator_plusplus.name";
    }
}
