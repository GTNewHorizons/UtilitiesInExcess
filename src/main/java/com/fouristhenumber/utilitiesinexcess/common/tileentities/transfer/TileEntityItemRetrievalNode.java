package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.ItemRetrievalNodeLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.NetworkLogic;

public class TileEntityItemRetrievalNode extends TileEntityTransferNodeBase
{
    @Override
    public NetworkLogic getLogic()
    {
        if (logic == null)
        {
            logic = new ItemRetrievalNodeLogic(this);
        }
        return logic;
    }
}
