package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityDrum extends TileEntity implements IFluidHandler {

    public FluidTank tank;
    private int capacity;

    public TileEntityDrum() {
        super();
    }

    public TileEntityDrum(int capacity) {
        super();
        this.capacity = capacity;
        this.tank = new FluidTank(capacity);
    }

    public void setFluid(FluidStack stack) {
        Fluid before = tank.getFluid() == null ? null
            : tank.getFluid()
                .getFluid();
        Fluid after = tank.getFluid() == null ? null
            : tank.getFluid()
                .getFluid();
        this.tank.setFluid(stack);
        markDirty();
        if (before != after) {
            renderUpdate();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.capacity = nbt.getInteger("capacity");
        this.tank = new FluidTank(capacity);
        if (nbt.hasKey("tank")) {
            tank.readFromNBT(nbt.getCompoundTag("tank"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("capacity", capacity);
        NBTTagCompound tankNbt = new NBTTagCompound();
        tank.writeToNBT(tankNbt);
        nbt.setTag("tank", tankNbt);
    }

    // IFluidHandler

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (resource == null) return 0;
        boolean wasEmpty = tank.getFluid() == null;
        int filled = tank.fill(resource, doFill);
        if (doFill && filled > 0) {
            markDirty();
            if (wasEmpty) renderUpdate();
        }
        return filled;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null) return null;
        FluidStack inTank = tank.getFluid();
        if (inTank == null || !resource.isFluidEqual(inTank)) return null;
        FluidStack drained = tank.drain(resource.amount, doDrain);
        if (doDrain && drained != null && drained.amount > 0) {
            markDirty();
            if (tank.getFluid() == null) renderUpdate();
        }
        return drained;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        FluidStack drained = tank.drain(maxDrain, doDrain);
        if (doDrain && drained != null && drained.amount > 0) {
            markDirty();
            if (tank.getFluid() == null) renderUpdate();
        }
        return drained;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        FluidStack current = tank.getFluid();
        return current == null || current.getFluid() == fluid;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        FluidStack current = tank.getFluid();
        return current != null && current.getFluid() == fluid;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { tank.getInfo() };
    }

    private void renderUpdate() {
        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 2, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        Fluid before = tank.getFluid() == null ? null
            : tank.getFluid()
                .getFluid();
        readFromNBT(pkt.func_148857_g());
        Fluid after = tank.getFluid() == null ? null
            : tank.getFluid()
                .getFluid();
        if (before != after) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }
}
