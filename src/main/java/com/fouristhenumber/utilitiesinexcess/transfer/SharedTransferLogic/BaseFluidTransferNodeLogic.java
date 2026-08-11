package com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public abstract class BaseFluidTransferNodeLogic<T extends INodeLogicHost> extends BaseNodeLogic<T, FluidStack>
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
}
