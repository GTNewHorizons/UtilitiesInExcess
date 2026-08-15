package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.ItemRetrievalNodeLogic;
import net.minecraft.item.ItemStack;

public class TileEntityItemRetrievalNode extends TileEntityTransferNodeBase<ItemRetrievalNodeLogic, ItemStack>
{
    @Override
    protected ItemRetrievalNodeLogic getLogic()
    {
        if (logic == null)
        {
            logic = new ItemRetrievalNodeLogic(this);
        }
        return logic;
    }
}
