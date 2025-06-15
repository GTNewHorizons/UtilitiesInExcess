package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntitySoundMuffler extends TileEntity {
    boolean active;
    boolean changed;

    public void enableMuffler() {
        UtilitiesInExcess.proxy.soundEventHandler.putSoundMuffler(worldObj.provider.dimensionId, xCoord, yCoord, zCoord);
    }

    public void disableMuffler() {
        UtilitiesInExcess.proxy.soundEventHandler.removeSoundMuffler(worldObj.provider.dimensionId, xCoord, yCoord, zCoord);
    }

    @Override
    public void updateEntity() {
        if (!worldObj.isRemote) return;
        if (!changed) return;

        if (active) enableMuffler(); else disableMuffler();
        changed = false;
    }

    public void onInputChanged() {
        if (worldObj.isRemote) return;

        boolean redstonePowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
        // Check our redstone state has changed
        if (redstonePowered == active) {
            active = !redstonePowered;
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            markDirty();
        }
    }

    public void onBlockBreak() {
        //UtilitiesInExcess.networkWrapper.sendToAll(new PacketMuffler(this, false));
    }


    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        active = nbt.getBoolean("active");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("active", active);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        UtilitiesInExcess.LOG.warn("Recieved on client");
        this.readFromNBT(pkt.func_148857_g());
        this.changed = true;
    }
}
