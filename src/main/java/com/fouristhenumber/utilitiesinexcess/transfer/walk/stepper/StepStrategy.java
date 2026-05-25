package com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.TransportType;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class StepStrategy
{
    public ForgeDirection fromDirection;
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

    public abstract ITransferNetworkComponent step(ITransferNetworkComponent currentComponent, ITransferNetworkComponent walkingComponent);
    public abstract ITransferNetworkComponent reset(ITransferNetworkComponent currentComponent, ITransferNetworkComponent walkingComponent);
}
