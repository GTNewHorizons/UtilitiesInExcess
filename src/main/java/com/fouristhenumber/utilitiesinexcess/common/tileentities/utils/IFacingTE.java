package com.fouristhenumber.utilitiesinexcess.common.tileentities.utils;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public interface IFacingTE {

    ForgeDirection getFacing();

    void setFacing(ForgeDirection newFacing);

    default void writeFacingToNBT(NBTTagCompound nbt) {
        if (getFacing() != null) {
            nbt.setInteger("facing", getFacing().ordinal());
        }
    }

    default void readFacingFromNBT(NBTTagCompound nbt) {
        setFacing(ForgeDirection.getOrientation(nbt.getInteger("facing")));
    }
}
