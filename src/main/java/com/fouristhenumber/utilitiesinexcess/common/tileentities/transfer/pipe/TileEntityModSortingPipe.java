package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityNetworkComponentBase;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.pipe.ModSortingPipeLogic;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityModSortingPipe extends TileEntityNetworkComponentBase<ModSortingPipeLogic>
{

    public TileEntityModSortingPipe()
    {
        logic = new ModSortingPipeLogic(this);
    }

    @Override
    public boolean canConnectToSide(ForgeDirection side)
    {
        return false;
    }
}
