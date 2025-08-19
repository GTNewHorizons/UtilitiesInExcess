package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityBlockUpdateDetector extends TileEntity {

    private boolean isProvidingPower = false;

    @Override
    public void updateEntity() {
        if (worldObj.isRemote) {
            return;
        }

        if (isProvidingPower) {
            isProvidingPower = false;
            notifyNeighbors();
        }

    }

    public void onNeighborUpdate() {
        isProvidingPower = true;
        notifyNeighbors();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord), 1);
    }

    public int getOutputPower() {
        return isProvidingPower ? 15 : 0;
    }

    private void notifyNeighbors() {
        worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        isProvidingPower = nbt.getBoolean("isProvidingPower");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("isProvidingPower", isProvidingPower);
    }

}
