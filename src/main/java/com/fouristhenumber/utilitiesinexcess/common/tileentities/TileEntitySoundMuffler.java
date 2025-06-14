package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntitySoundMuffler extends TileEntity {
    boolean powered;

    @Override
    public boolean canUpdate() {
        return false;
    }

    public void onInputChanged() {
        boolean nowPowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
        if (nowPowered != powered) {
            powered = nowPowered;
            if (powered) {
            }
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        powered = nbt.getBoolean("powered");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("powered", powered);
    }
}
