package com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.TransportType;

import java.util.List;

public interface TargetResolver<T>
{
    List<T> getValidTargets(
        ITransferNetworkComponent from);
}
