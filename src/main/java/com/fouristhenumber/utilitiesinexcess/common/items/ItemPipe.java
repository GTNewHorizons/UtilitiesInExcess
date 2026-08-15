package com.fouristhenumber.utilitiesinexcess.common.items;

import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.PipeType;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemPipe extends BaseTransferItemBlock
{
    public ItemPipe(Block block)
    {
        super(block);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return PipeType.values()[stack.getItemDamage()].getName();
    }
}
