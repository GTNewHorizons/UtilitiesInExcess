package com.fouristhenumber.utilitiesinexcess.transfer.walk;

import java.util.List;

import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidHandler;

import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.*;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting.FluidTargetResolver;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting.TargetResolver;

public class FluidWalker extends WalkerBase<IFluidHandler> {

    TargetResolver<IFluidHandler> targeter;

    public FluidWalker(IWalkingComponent walkingComponent) {
        super(walkingComponent);
        stepper = new RandomStepper();
        targeter = new FluidTargetResolver();
    }

    @Override
    public void step(World world) {
        walkerPos = stepper.step(world, walkerPos, walkingComponent);
    }

    @Override
    public List<TargetResolver.Target<IFluidHandler>> getValidTargets(World world) {
        return targeter.getValidTargets(world, walkerPos, walkingComponent, stepper.fromDirection);
    }

    @Override
    public void reset() {
        stepper.reset(walkerPos, walkingComponent);
    }
}
