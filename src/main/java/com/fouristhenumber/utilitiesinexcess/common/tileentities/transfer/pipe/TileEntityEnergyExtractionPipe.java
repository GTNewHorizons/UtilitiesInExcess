package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityNetworkComponentBase;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.pipe.EnergyExtractionPipeLogic;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityEnergyExtractionPipe extends TileEntityNetworkComponentBase<EnergyExtractionPipeLogic>
{
    public TileEntityEnergyExtractionPipe()
    {
        logic = new EnergyExtractionPipeLogic(this);
    }

    @Override
    public boolean canConnectToSide(ForgeDirection side) {
        return true;
    }
}
