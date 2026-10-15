package com.fouristhenumber.utilitiesinexcess.common.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.PipeType;

public class ItemPipe extends BaseTransferItemBlock {

    public ItemPipe(Block block) {
        super(block);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "tile." + PipeType.values()[stack.getItemDamage()].getName();
    }
}
