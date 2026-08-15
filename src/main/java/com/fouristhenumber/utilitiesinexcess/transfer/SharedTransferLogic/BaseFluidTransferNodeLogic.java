package com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public abstract class BaseFluidTransferNodeLogic<T extends IWalkingComponent<FluidStack>> extends BaseNodeLogic<T, FluidStack>
{
    public static final int maxFluidAmount = 8000;
    public static final int DEFAULT_MAX_DRAIN_AMOUNT = 200;
    public int maxDrainAmount = DEFAULT_MAX_DRAIN_AMOUNT;
    public FluidTank buffer = new FluidTank(maxFluidAmount);

    public BaseFluidTransferNodeLogic(T host) {
        super(host);
    }

    @Override
    public FluidStack getWalkingObject() {
        return buffer.getFluid();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        NBTTagCompound fluidTag = new NBTTagCompound();
        buffer.writeToNBT(fluidTag);
        nbt.setTag("Fluid", fluidTag);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        buffer.readFromNBT(nbt.getCompoundTag("Fluid"));
    }

    @Override
    public void writeDesc(MCDataOutput output)
    {
        super.writeDesc(output);
        output.writeFluidStack(buffer.getFluid());
    }

    @Override
    public void readDesc(MCDataInput input)
    {
        super.readDesc(input);
        buffer.setFluid(input.readFluidStack());
    }

}
