package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidHandler;

import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockTransferPipe;

import cofh.api.energy.IEnergyReceiver;

public class TileEntityTransferPipe extends TileEntity {

    private int connectionsMask = 0;

    public int getConnectionsMask() {
        return connectionsMask;
    }

    public boolean updateConnections(World world, int x, int y, int z) {
        int old = connectionsMask;
        int mask = 0;
        if (connectsTo(world, x + 1, y, z)) mask |= 1;
        if (connectsTo(world, x - 1, y, z)) mask |= 1 << 1;
        if (connectsTo(world, x, y + 1, z)) mask |= 1 << 2;
        if (connectsTo(world, x, y - 1, z)) mask |= 1 << 3;
        if (connectsTo(world, x, y, z + 1)) mask |= 1 << 4;
        if (connectsTo(world, x, y, z - 1)) mask |= 1 << 5;

        connectionsMask = mask;
        if (old != mask) {
            markDirty();
            return true;
        }
        return false;
    }

    private boolean connectsTo(World world, int x, int y, int z) {
        Block b = world.getBlock(x, y, z);
        if (b instanceof BlockTransferPipe) return true;
        TileEntity te = world.getTileEntity(x, y, z);

        return te instanceof IInventory || te instanceof IFluidHandler || te instanceof IEnergyReceiver;
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
}
