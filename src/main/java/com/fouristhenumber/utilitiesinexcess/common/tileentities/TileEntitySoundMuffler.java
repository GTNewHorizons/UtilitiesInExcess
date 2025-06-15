package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.network.PacketSoundMuffler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntitySoundMuffler extends TileEntity {
    boolean redstonepowered;
    boolean active;

    @Override
    public void updateEntity() {
        if (worldObj.isRemote) return;
        if (active && redstonepowered) {
            disableMuffler();
        } else if (!active && !redstonepowered) {
            enableMuffler();
        }
    }

    public void onInputChanged() {
        boolean nowPowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
        if (nowPowered != redstonepowered) {
            redstonepowered = nowPowered;
            markDirty();
        }
    }

    public void enableMuffler() {
        UtilitiesInExcess.networkWrapper.sendToAll(new PacketSoundMuffler(this, true));
        active = true;
    }

    public void disableMuffler() {
        UtilitiesInExcess.networkWrapper.sendToAll(new PacketSoundMuffler(this, false));
        active = false;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        redstonepowered = nbt.getBoolean("powered");
        active = nbt.getBoolean("active");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("powered", redstonepowered);
        nbt.setBoolean("active", active);
    }
}
