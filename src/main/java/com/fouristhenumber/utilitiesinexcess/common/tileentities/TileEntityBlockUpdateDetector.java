package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import net.minecraft.tileentity.TileEntity;

public class TileEntityBlockUpdateDetector extends TileEntity {

    private boolean isProvidingPower = false;
    private int pulseTimer = 0;

    @Override
    public void updateEntity() {
        if (worldObj.isRemote) {
            return;
        }

        if (isProvidingPower && pulseTimer > 0) {
            pulseTimer--;
            if (pulseTimer <= 0) {
                isProvidingPower = false;
                worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
            }
        }
    }

    public void onNeighborUpdate() {
        if (!isProvidingPower) {
            isProvidingPower = true;
            pulseTimer++;
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
            worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, getBlockType(), 1);
        }
    }

    public int getOutputPower() {
        return isProvidingPower ? 15 : 0;
    }
}
