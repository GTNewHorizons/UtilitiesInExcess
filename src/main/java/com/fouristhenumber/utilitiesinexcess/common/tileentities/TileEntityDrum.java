package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;

public class TileEntityDrum extends TileEntity implements IFluidHandler {

    public FluidTank tank;

    public TileEntityDrum(int capacity) {
        super();
        this.tank = new FluidTank(capacity);
    }

    int ticker = 0;

    @Override
    public void updateEntity() {
        this.ticker++;
        if (this.ticker % 20 == 0) {
            UtilitiesInExcess.LOG.info("Hello from Drum");
            if (tank.getInfo().fluid != null && tank.getInfo().fluid.getFluid() != null) {
                UtilitiesInExcess.LOG.info(tank.getInfo().fluid.getFluid());
                UtilitiesInExcess.LOG.info(tank.getInfo().fluid.amount);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        tank.readFromNBT(nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        tank.writeToNBT(nbt);
    }

    // IFluidHandler implementation

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (resource == null) return 0;
        int filled = tank.fill(resource, doFill);
        if (doFill && filled > 0) markDirty();
        return filled;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null || !resource.isFluidEqual(tank.getFluid())) return null;
        FluidStack drained = tank.drain(resource.amount, doDrain);
        if (doDrain && drained != null && drained.amount > 0) markDirty();
        return drained;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        FluidStack drained = tank.drain(maxDrain, doDrain);
        if (doDrain && drained != null && drained.amount > 0) markDirty();
        return drained;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return true;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        FluidStack fluidInTank = tank.getFluid();
        return fluidInTank != null && fluidInTank.getFluid() == fluid;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { tank.getInfo() };
    }
}
