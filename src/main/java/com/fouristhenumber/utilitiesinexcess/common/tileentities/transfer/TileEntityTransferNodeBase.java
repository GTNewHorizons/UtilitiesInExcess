package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.INodeLogicHost;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.ITransferNetworkLogic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityTransferNodeBase<T> extends TileEntityNetworkComponentBase<T>
    implements INodeLogicHost
{
    @Override
    public ForgeDirection getFacing()
    {
        return ForgeDirection.getOrientation(worldObj.getBlockMetadata(xCoord, yCoord, zCoord) & 7);
    }


    public abstract void updateSource();
}
