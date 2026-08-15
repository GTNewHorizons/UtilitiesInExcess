package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.ItemTransferNodeLogic;
import net.minecraft.item.ItemStack;

public class TileEntityItemTransferNode extends TileEntityTransferNodeBase<ItemTransferNodeLogic, ItemStack>
{
    @Override
    protected ItemTransferNodeLogic getLogic()
    {
        if (logic == null)
        {
            logic = new ItemTransferNodeLogic(this);
        }
        return logic;
    }
}
