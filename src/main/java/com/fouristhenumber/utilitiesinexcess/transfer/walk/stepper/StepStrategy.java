package com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper;

import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.TransportType;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class StepStrategy
{
    public ForgeDirection fromDirection = ForgeDirection.UNKNOWN;
    public TransportType transporting;

    public enum Strategy
    {
        RANDOM,
        BFS,
        DFS,
        ROUND_ROBIN
    }

    StepStrategy(TransportType type)
    {
        this.transporting = type;
    }

    public abstract BlockPos step(World world, BlockPos walkerPos, IWalkingComponent walkingComponent);
    public abstract BlockPos reset(BlockPos walkerPos, IWalkingComponent walkingComponent);
}
