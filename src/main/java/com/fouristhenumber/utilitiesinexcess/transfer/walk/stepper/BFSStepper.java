package com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.TransportType;

public class BFSStepper extends StepStrategy
{

    public BFSStepper(TransportType transporting) {
        super(transporting);
    }

    @Override
    public ITransferNetworkComponent step(ITransferNetworkComponent currentComponent, IWalkingComponent walkingComponent)
    {
        return null;
    }

    @Override
    public ITransferNetworkComponent reset(ITransferNetworkComponent currentComponent, IWalkingComponent walkingComponent)
    {
        return null;
    }
}
