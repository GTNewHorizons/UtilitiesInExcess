package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.INodeLogicHost;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityTransferNodeBase<T> extends TileEntityNetworkComponentBase<T>
    implements INodeLogicHost
{
    public boolean init = false;

    @Override
    public ForgeDirection getFacing()
    {
        return ForgeDirection.getOrientation(worldObj.getBlockMetadata(xCoord, yCoord, zCoord) & 7);
    }
}
