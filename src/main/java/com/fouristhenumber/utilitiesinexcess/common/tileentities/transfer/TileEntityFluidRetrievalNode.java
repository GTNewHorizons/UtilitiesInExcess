package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.FluidRetrievalNodeLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.NetworkLogic;

public class TileEntityFluidRetrievalNode extends TileEntityTransferNodeBase
{
    @Override
    public NetworkLogic getLogic()
    {
        if (logic == null)
        {
            logic = new FluidRetrievalNodeLogic(this);
        }
        return logic;
    }
}
