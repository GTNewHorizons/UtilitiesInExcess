package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.FluidRetrievalNodeLogic;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityFluidRetrievalNode extends TileEntityTransferNodeBase<FluidRetrievalNodeLogic, FluidStack>
{
    @Override
    protected FluidRetrievalNodeLogic getLogic()
    {
        if (logic == null)
        {
            logic = new FluidRetrievalNodeLogic(this);
        }
        return logic;
    }
}
