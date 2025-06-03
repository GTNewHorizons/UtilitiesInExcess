package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityRedstoneClock extends TileEntity {

    private boolean powered = false;
    private boolean outputOn = false;
    private int timer = 0;
    private final int TIMER_FREQUENCY_IN_TICKS = 20;

    @Override
    public void updateEntity() {
        if (worldObj.isRemote) return;

        if (!powered) {
            timer = (timer + 1) % TIMER_FREQUENCY_IN_TICKS;
            boolean newState = (timer < 2);
            if (newState != outputOn) {
                outputOn = newState;
                notifyNeighbors();
            }
        } else {
            if (outputOn) {
                outputOn = false;
                notifyNeighbors();
            }
            timer = 0;
        }
    }

    public void onInputChanged() {
        boolean nowPowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
        if (nowPowered != powered) {
            powered = nowPowered;
            if (powered) {
                outputOn = false;
                notifyNeighbors();
            }
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord), 1);
        }
    }

    // Return 0 or 15 for our block’s power output
    public int getOutputPower() {
        return outputOn ? 15 : 0;
    }

    private void notifyNeighbors() {
        worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        powered = nbt.getBoolean("powered");
        outputOn = nbt.getBoolean("outputOn");
        timer = nbt.getInteger("timer");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("powered", powered);
        nbt.setBoolean("outputOn", outputOn);
        nbt.setInteger("timer", timer);
    }
}
