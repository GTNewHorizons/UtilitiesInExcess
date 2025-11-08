package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityDrum extends TileEntity implements IFluidHandler {

    public final FluidTank tank;
    public final int DEFAULT_CAPACITY = 16000;


    public TileEntityDrum() {
        tank = new FluidTank(DEFAULT_CAPACITY);
    }

    public TileEntityDrum(int capacity) {
        super();
        this.tank = new FluidTank(capacity);
    }

    public void setTank(FluidTank tank) {
        setFluid(tank.getFluid());
    }

    public void setFluid(FluidStack stack) {
        this.tank.setFluid(stack);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey("tank")) {
            NBTTagCompound tankNbt = nbt.getCompoundTag("tank");
            tank.readFromNBT(tankNbt);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagCompound tankNbt = new NBTTagCompound();
        tank.writeToNBT(tankNbt);
        nbt.setTag("tank", tankNbt);
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
