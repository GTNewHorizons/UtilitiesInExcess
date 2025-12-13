package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityRedstoneClock extends TileEntity {

    private boolean powered = false;
    private boolean outputOn = false;
    private int timer = 0;

    @Override
    public void updateEntity() {
        if (worldObj.isRemote) return;

        boolean prevOutput = outputOn;

        outputOn = false;
        // If the clock is being powered outside the window where it is active, it must have come from an external
        // source
        timer--;
        if (timer <= 1) {
            outputOn = true;
            if (timer == 0) timer = 20;
        } else if (powered) {
            timer = 20;
        }

        if (prevOutput != outputOn) {
            notifyNeighbors();
        }
    }

    public void onInputChanged() {
        powered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
    }

    private void updateBlockState() {
        int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        int newMeta = outputOn ? 1 : 0;

        if (meta != newMeta) {
            worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, newMeta, 3);
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    public int getOutputPower() {
        return outputOn ? 15 : 0;
    }

    private void notifyNeighbors() {
        worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
        updateBlockState();
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
