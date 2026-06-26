package com.fouristhenumber.utilitiesinexcess.transfer.walk;

import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.RandomStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.StepStrategy;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.TargetResolver;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.List;

public class FluidWalker extends WalkerBase<IFluidHandler, FluidStack>
{
    StepStrategy stepper;
    public FluidWalker(IWalkingComponent<FluidStack> walkingComponent) {
        super(walkingComponent);
        stepper = new RandomStepper(TransportType.FLUID);
    }

    @Override
    public void step() {

    }

    @Override
    public void reset() {

    }

    @Override
    public List<TargetResolver.Target<IFluidHandler>> getValidTargets() {
        return List.of();
    }

    @Override
    public int getInsertLimit() {
        return 0;
    }
}
