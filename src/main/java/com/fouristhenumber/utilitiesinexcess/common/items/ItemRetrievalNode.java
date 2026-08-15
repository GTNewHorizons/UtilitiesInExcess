package com.fouristhenumber.utilitiesinexcess.common.items;

import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockRetrievalNode;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemRetrievalNode extends BaseTransferItemBlock
{
    public ItemRetrievalNode(Block block)
    {
        super(block);

    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return BlockRetrievalNode.RetrievalNodeType.values()[stack.getItemDamage()].getName();
    }
}
