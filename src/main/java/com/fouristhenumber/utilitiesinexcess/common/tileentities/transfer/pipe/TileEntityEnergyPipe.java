package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityNetworkComponentBase;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.pipe.EnergyPipeLogic;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityEnergyPipe extends TileEntityNetworkComponentBase<EnergyPipeLogic>
{
    public TileEntityEnergyPipe()
    {
        logic = new EnergyPipeLogic(this);
    }

    @Override
    public boolean canConnectToSide(ForgeDirection side) {
        return true;
    }
}
