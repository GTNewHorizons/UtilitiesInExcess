package com.fouristhenumber.utilitiesinexcess.transfer.walk;

import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.*;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting.FluidTargetResolver;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting.TargetResolver;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.List;

public class FluidWalker extends WalkerBase<IFluidHandler, FluidStack>
{
    StepStrategy stepper;
    TargetResolver<IFluidHandler> targeter;

    public FluidWalker(IWalkingComponent<FluidStack> walkingComponent) {
        super(walkingComponent);
        stepper = new RandomStepper(TransportType.FLUID);
        targeter = new FluidTargetResolver();
    }

    @Override
    public void step(World world)
    {
        walkerPos = stepper.step(world, walkerPos, walkingComponent);
    }

    @Override
    public List<TargetResolver.Target<IFluidHandler>> getValidTargets(World world) {
        return targeter.getValidTargets(world, walkerPos, walkingComponent, stepper.fromDirection);
    }

    @Override
    public void reset()
    {
        stepper.reset(walkerPos, walkingComponent);
    }
}
