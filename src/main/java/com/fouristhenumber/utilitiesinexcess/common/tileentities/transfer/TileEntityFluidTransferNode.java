package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.FluidTransferNodeLogic;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityFluidTransferNode extends TileEntityTransferNodeBase<FluidTransferNodeLogic, FluidStack>
{
    @Override
    protected FluidTransferNodeLogic getLogic()
    {
        if (logic == null)
        {
            logic = new FluidTransferNodeLogic(this);
        }
        return logic;
    }

}
