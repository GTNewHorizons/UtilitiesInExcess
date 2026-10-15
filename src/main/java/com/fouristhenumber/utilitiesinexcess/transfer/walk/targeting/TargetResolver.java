package com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting;

import java.util.List;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

public interface TargetResolver<T> {

    List<Target<T>> getValidTargets(World world, BlockPos walkerPos, IWalkingComponent walking, ForgeDirection fromDir);

    class Target<T> {

        public final T handler;
        public final int side;
        public final int x;
        public final int y;
        public final int z;

        public Target(T handler, int side, int x, int y, int z) {
            this.handler = handler;
            this.side = side;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
