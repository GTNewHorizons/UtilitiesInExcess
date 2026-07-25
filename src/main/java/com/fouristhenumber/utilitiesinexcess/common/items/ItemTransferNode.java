package com.fouristhenumber.utilitiesinexcess.common.items;

import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockTransferNode;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemTransferNode extends ItemBlock
{
    public ItemTransferNode(Block block)
    {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return BlockTransferNode.TransferNodeType.values()[stack.getItemDamage()].getName();
    }
}
