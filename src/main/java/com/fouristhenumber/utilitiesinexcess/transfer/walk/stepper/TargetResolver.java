package com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;

import java.util.List;

public interface TargetResolver<T>
{
    List<Target<T>> getValidTargets(
        ITransferNetworkComponent from);

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
