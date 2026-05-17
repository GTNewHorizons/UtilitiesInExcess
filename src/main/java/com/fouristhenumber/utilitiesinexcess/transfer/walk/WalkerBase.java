package com.fouristhenumber.utilitiesinexcess.transfer.walk;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.TargetResolver;

import java.util.List;
import java.util.Random;

public abstract class WalkerBase<T>
{
    protected ITransferNetworkComponent walkingComponent;
    protected ITransferNetworkComponent currentComponent;

    Random rand = new Random();
    WalkerBase(ITransferNetworkComponent walkingComponent)
    {
        this.walkingComponent = walkingComponent;
        this.currentComponent = walkingComponent;
    }
    public abstract void step();

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

    public abstract void reset();

    public abstract TargetResolver.Target<T> getValidTarget();
}
