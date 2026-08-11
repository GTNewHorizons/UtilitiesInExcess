package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.ConversionRegistry;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.ItemTransferNodeLogic;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemTransferNodePart extends BaseNodePart<ItemTransferNodeLogic, ItemStack>
{
    public ItemTransferNodePart(int meta) {
        super(meta);
    }

    @Override
    public Block getBlock() {
        return ModBlocks.TRANSFER_NODE.get();
    }

    @Override
    protected ItemTransferNodeLogic getLogic() {
        if (logic == null)
        {
            logic = new ItemTransferNodeLogic(this);
        }
        return logic;
    }

    @Override
    public String getType() {
        return ConversionRegistry.ItemTransferNode.getName();
    }
}
