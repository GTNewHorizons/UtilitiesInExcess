package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityNetworkComponentBase;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.pipe.HyperRationingPipeLogic;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityHyperRationingPipe extends TileEntityNetworkComponentBase<HyperRationingPipeLogic>
{
    public TileEntityHyperRationingPipe()
    {
        logic = new HyperRationingPipeLogic(this);
    }

    @Override
    public boolean canConnectToSide(ForgeDirection side) {
        return true;
    }
}
