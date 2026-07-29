package com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;

public abstract class NetworkLogic<T extends ITransferNetworkComponent>
{
    protected T host;

    public NetworkLogic(T host)
    {
        this.host = host;
    }
}
