package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityNetworkComponentBase;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.PipeLogic;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityTransferPipe extends TileEntityNetworkComponentBase<PipeLogic>
{

    public TileEntityTransferPipe()
    {
        logic = new PipeLogic(this);
    }

    @Override
    public boolean canConnectToSide(ForgeDirection side)
    {
        return true;
    }
}
