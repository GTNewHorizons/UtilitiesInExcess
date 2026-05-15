package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.INodeLogicHost;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityTransferNodeBase extends TileEntityNetworkComponentBase
    implements INodeLogicHost
{

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public boolean canConnectToSide(ForgeDirection side)
    {
        return worldObj.getBlockMetadata(xCoord, yCoord, zCoord) != side.ordinal();
    }

    @Override
    public ForgeDirection getFacing() {
        return ForgeDirection.getOrientation(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
    }
}
