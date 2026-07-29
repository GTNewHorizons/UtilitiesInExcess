package com.fouristhenumber.utilitiesinexcess.transfer.walk;

import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting.TargetResolver;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public abstract class WalkerBase<T, E>
{
    protected IWalkingComponent<E> walkingComponent;
    BlockPos walkerPos;

    WalkerBase(IWalkingComponent<E> walkingComponent)
    {
        this.walkingComponent = walkingComponent;
        walkerPos = new BlockPos(walkingComponent.getX(), walkingComponent.getY(), walkingComponent.getZ());
    }
    public abstract void step(World world);

    public String getLocationString()
    {
        return "x: " +
            (walkerPos.x - walkingComponent.getX()) +
            " y: " +
            (walkerPos.y - walkingComponent.getY()) +
            " z: " +
            (walkerPos.z - walkingComponent.getZ());
    }

    public abstract void reset();

    public abstract List<TargetResolver.Target<T>> getValidTargets(World world);

    // TODO Double check that fluids aren't affected by rationing pipes
    public int getInsertLimit(World world, int x, int y, int z)
    {
        return -1;
    }
}
