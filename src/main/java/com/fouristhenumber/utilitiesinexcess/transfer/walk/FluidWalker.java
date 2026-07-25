package com.fouristhenumber.utilitiesinexcess.transfer.walk;

import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.*;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.List;

public class FluidWalker extends WalkerBase<IFluidHandler, FluidStack>
{
    StepStrategy stepper;
    TargetResolver<IFluidHandler> targeter = new FluidTargetResolver();

    public FluidWalker(IWalkingComponent<FluidStack> walkingComponent) {
        super(walkingComponent);
        stepper = new RandomStepper(TransportType.FLUID);
    }

    @Override
    public void step() {
        currentComponent = stepper.step(currentComponent, walkingComponent);
    }

    @Override
    public void reset() {
        currentComponent = stepper.reset(currentComponent, walkingComponent);

    }

    @Override
    public List<TargetResolver.Target<IFluidHandler>> getValidTargets() {
        return targeter.getValidTargets(currentComponent, walkingComponent, stepper.fromDirection);
    }

    // TODO Double check that fluids aren't affected by rationing pipes
    @Override
    public int getInsertLimit() {
        return -1;
    }
}
