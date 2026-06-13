package com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.IWalkingComponent;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public interface TargetResolver<T>
{
    List<Target<T>> getValidTargets(
        ITransferNetworkComponent from, IWalkingComponent<?> walking, ForgeDirection fromDir);

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
