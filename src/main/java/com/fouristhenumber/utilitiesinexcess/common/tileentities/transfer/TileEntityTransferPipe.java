package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.ITransferNetworkLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.NetworkLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.PipeLogic;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityTransferPipe extends TileEntityNetworkComponentBase
{
    private final NetworkLogic logic;

    public TileEntityTransferPipe()
    {
        logic = new PipeLogic();
    }

    @Override
    public boolean canConnectToSide(ForgeDirection side) {
        return true;
    }

    @Override
    protected ITransferNetworkLogic getNetworkLogic() {
        return logic;
    }
}
