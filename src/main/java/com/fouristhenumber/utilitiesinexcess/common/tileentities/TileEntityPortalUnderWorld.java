package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityPortalUnderWorld extends TileEntity {

    /// Only set for outside->underworld portals
    /// Underworld->outside uses [UnderWorldSourceProperty] instead of looking for a specific block.
    public int destX, destY, destZ;
    public boolean hasDest;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        this.destX = compound.getInteger("destX");
        this.destY = compound.getInteger("destY");
        this.destZ = compound.getInteger("destZ");
        this.hasDest = compound.getBoolean("hasDest");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setInteger("destX", destX);
        compound.setInteger("destY", destY);
        compound.setInteger("destZ", destZ);
        compound.setBoolean("hasDest", hasDest);
    }
}
