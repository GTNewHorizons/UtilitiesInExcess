package com.fouristhenumber.utilitiesinexcess.transfer.walk;

import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.StepStrategy;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting.TargetResolver;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public abstract class WalkerBase<T, E>
{
    protected IWalkingComponent<E> walkingComponent;
    BlockPos walkerPos;
    protected StepStrategy stepper;

    WalkerBase(IWalkingComponent<E> walkingComponent)
    {
        this.walkingComponent = walkingComponent;
    }

    public void init()
    {
        walkerPos = new BlockPos(walkingComponent.getX(), walkingComponent.getY(), walkingComponent.getZ());
    }

    public void setStepper(StepStrategy stepper)
    {
        this.stepper = stepper;
        this.reset();
    }
    public abstract void step(World world);

    public String getLocationString()
    {
        if (walkerPos != null)
        {
            return "x: " +
                (walkerPos.x - walkingComponent.getX()) +
                " y: " +
                (walkerPos.y - walkingComponent.getY()) +
                " z: " +
                (walkerPos.z - walkingComponent.getZ());
        }
        else
        {
            return "x: 0 y: 0 z: 0";
        }
    }

    public abstract void reset();

    public abstract List<TargetResolver.Target<T>> getValidTargets(World world);

    // TODO Double check that fluids aren't affected by rationing pipes
    public int getInsertLimit(World world, int x, int y, int z)
    {
        return -1;
    }
}
