package com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;

public abstract class DefaultNetworkLogic
    extends NetworkLogic<ITransferNetworkComponent>
{
    public DefaultNetworkLogic(ITransferNetworkComponent host) {
        super(host);
    }
}
