package com.fouristhenumber.utilitiesinexcess.transfer.walk;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class WalkerBase
{
    protected ITransferNetworkComponent walkingComponent;
    protected ITransferNetworkComponent currentComponent;

    protected ForgeDirection fromDirection;

    WalkerBase(ITransferNetworkComponent originComponent)
    {
        this.walkingComponent = originComponent;
        this.currentComponent = originComponent;
    }

    public abstract void step();

    public ITransferNetworkComponent getCurrentComponent()
    {
        return currentComponent;
    }

    public String getLocationString()
    {
        StringBuilder location = new StringBuilder();
        location.append("x: ");
        location.append(currentComponent.getX() - walkingComponent.getX());
        location.append(" y: ");
        location.append(currentComponent.getY() - walkingComponent.getY());
        location.append(" z: ");
        location.append(currentComponent.getZ() - walkingComponent.getZ());
        return location.toString();
    }

    public void reset()
    {
        currentComponent = walkingComponent;
    }
}
