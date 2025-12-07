package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.items;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.compat.exu.postea.IPosteaTransformation;

public class EnderLilyTransformation implements IPosteaTransformation {

    @Override
    public void addItemRemappings(Map<String, Item> remappings) {
        remappings.put("ExtraUtilities:plant/ender_lilly", ModItems.ENDER_LOTUS_SEED.get());
    }

    @Override
    public void addBlockRemappings(Map<String, Block> remappings) {
        remappings.put("ExtraUtilities:plant/ender_lilly", ModBlocks.ENDER_LOTUS.get());
    }
}
