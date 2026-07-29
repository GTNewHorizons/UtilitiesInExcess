package com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting;

import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public interface TargetResolver<T>
{

    List<Target<T>> getValidTargets(World world, BlockPos walkerPos, IWalkingComponent<?> walking, ForgeDirection fromDir);

    class Target<T>
    {
        public final T handler;
        public final int side;

        public Target(T handler, int side)
        {
            this.handler = handler;
            this.side = side;
        }
    }
}
