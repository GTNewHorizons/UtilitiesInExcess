package com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper;

import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import net.minecraft.world.World;

public class RoundRobinStepper extends StepStrategy
{
    public RoundRobinStepper() {}

    @Override
    public BlockPos step(World world, BlockPos walkerPos, IWalkingComponent walkingComponent) {
        return null;
    }

    @Override
    public BlockPos reset(BlockPos walkerPos, IWalkingComponent walkingComponent) {
        return null;
    }

}
