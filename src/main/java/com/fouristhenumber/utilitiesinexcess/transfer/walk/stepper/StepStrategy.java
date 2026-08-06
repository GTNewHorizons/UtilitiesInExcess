package com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper;

import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class StepStrategy
{
    public ForgeDirection fromDirection = ForgeDirection.UNKNOWN;

    public enum Strategy
    {
        RANDOM,
        BFS,
        DFS,
        ROUND_ROBIN
    }

    StepStrategy()
    {
    }

    public abstract BlockPos step(World world, BlockPos walkerPos, IWalkingComponent walkingComponent);
    public abstract BlockPos reset(BlockPos walkerPos, IWalkingComponent walkingComponent);
}
