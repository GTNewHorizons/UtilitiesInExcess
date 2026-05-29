package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityNetworkComponentBase;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.pipe.SortingPipeLogic;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntitySortingPipe extends TileEntityNetworkComponentBase<SortingPipeLogic>
{

    public TileEntitySortingPipe()
    {
        logic = new SortingPipeLogic(this);
    }

    @Override
    public boolean canConnectToSide(ForgeDirection side)
    {
        return true;
    }

    @Override
    public int getInsertLimit() {
        return logic.getMaxInsertable();
    }
}
