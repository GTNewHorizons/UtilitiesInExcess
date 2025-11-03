package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyReceiver;

public class TileEntityTrashCanEnergy extends TileEntity implements IEnergyReceiver {

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        return maxReceive;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return 0;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }
}
