package com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic;


import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;

public class PipeLogic extends NetworkLogic
{

    public PipeLogic(ITransferNetworkComponent host) {
        super(host);
    }

    @Override
    public boolean canConnectEnergy() {
        return true;
    }

    @Override
    public boolean canConnectFluid() {
        return true;
    }

    @Override
    public boolean canConnectItem() {
        return true;
    }
}
