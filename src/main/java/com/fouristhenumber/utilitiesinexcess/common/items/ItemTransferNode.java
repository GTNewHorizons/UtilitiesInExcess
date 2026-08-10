package com.fouristhenumber.utilitiesinexcess.common.items;

import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockTransferNode;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemTransferNode extends BaseTransferItemBlock
{
    public ItemTransferNode(Block block)
    {
        super(block);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return BlockTransferNode.TransferNodeType.values()[stack.getItemDamage()].getName();
    }
}
