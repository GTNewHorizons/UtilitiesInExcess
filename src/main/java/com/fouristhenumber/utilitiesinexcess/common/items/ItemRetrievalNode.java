package com.fouristhenumber.utilitiesinexcess.common.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockNodeBase;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockRetrievalNode;

public class ItemRetrievalNode extends BaseTransferItemBlock {

    public ItemRetrievalNode(Block block) {
        super(block);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "tile."
            + BlockRetrievalNode.RetrievalNodeType.values()[BlockNodeBase.getType(stack.getItemDamage())].getName();
    }
}
