package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.INodeLogicHost;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.NetworkLogic;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityTransferNodeBase<T extends NetworkLogic<? extends ITransferNetworkComponent>> extends TileEntityNetworkComponentBase<T>
    implements INodeLogicHost
{
    @Override
    public ForgeDirection getFacing()
    {
        return ForgeDirection.getOrientation(worldObj.getBlockMetadata(xCoord, yCoord, zCoord) & 7);
    }
}
