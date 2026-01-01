package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityTransferNodeBase extends TileEntity implements ITransferNetworkComponent {

    private int connectionsMask = 0;

    public int getConnectionsMask() {
        return connectionsMask;
    }

    public boolean updateConnections(World world, int x, int y, int z) {
        int old = connectionsMask;
        int mask = 0;
        if (canConnectTo(world, x + 1, y, z, 5)) mask |= 1;
        if (canConnectTo(world, x - 1, y, z, 4)) mask |= 1 << 1;
        if (canConnectTo(world, x, y + 1, z, 1)) mask |= 1 << 2;
        if (canConnectTo(world, x, y - 1, z, 0)) mask |= 1 << 3;
        if (canConnectTo(world, x, y, z + 1, 3)) mask |= 1 << 4;
        if (canConnectTo(world, x, y, z - 1, 2)) mask |= 1 << 5;

        connectionsMask = mask;
        if (old != mask) {
            markDirty();
            return true;
        }
        return false;
    }

    @Override
    public boolean canConnectTo(World world, int x, int y, int z, int side) {
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("connectionsMask", connectionsMask);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        connectionsMask = nbt.getInteger("connectionsMask");
    }

    @Override
    public Packet getDescriptionPacket() {
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
    public boolean canConnectFrom(ForgeDirection side) {
        return side.getOpposite()
            .ordinal() != blockMetadata;
    }
}
