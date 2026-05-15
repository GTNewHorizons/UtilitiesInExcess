package com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.TransportType;

public class DFSStepper extends StepStrategy
{

    public DFSStepper(TransportType transporting) {
        super(transporting);
    }

    @Override
    public ITransferNetworkComponent step(ITransferNetworkComponent currentComponent, ITransferNetworkComponent walkingComponent) {
        return null;
    }

    @Override
    public ITransferNetworkComponent reset(ITransferNetworkComponent currentComponent, ITransferNetworkComponent walkingComponent) {
        return null;
    }
}
