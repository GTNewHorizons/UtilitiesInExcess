package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import codechicken.lib.data.MCDataInput;
import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.ConversionRegistry;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.ItemRetrievalNodeLogic;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemRetrievalNodePart extends BaseNodePart<ItemRetrievalNodeLogic, ItemStack>
{
    public ItemRetrievalNodePart(int meta) {
        super(meta);
    }

    public ItemRetrievalNodePart(MCDataInput packet)
    {
        super(packet.readInt());
        logic.readDesc(packet);
    }

    @Override
    public Block getBlock() {
        return ModBlocks.RETRIEVAL_NODE.get();
    }

    @Override
    protected ItemRetrievalNodeLogic getLogic() {
        if (logic == null)
        {
            logic = new ItemRetrievalNodeLogic(this);
        }
        return logic;
    }

    @Override
    public String getType() {
        return ConversionRegistry.ItemRetrievalNode.getName();
    }
}
