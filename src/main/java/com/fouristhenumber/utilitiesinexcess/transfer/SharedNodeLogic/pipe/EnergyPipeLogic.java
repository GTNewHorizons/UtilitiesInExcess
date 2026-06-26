package com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.pipe;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.DefaultNetworkLogic;

public class EnergyPipeLogic extends DefaultNetworkLogic
{

    public EnergyPipeLogic(ITransferNetworkComponent host) {
        super(host);
    }

    @Override
    public boolean canConnectEnergy() {
        return true;
    }

    @Override
    public boolean canConnectFluid() {
        return false;
    }

    @Override
    public boolean canConnectItem() {
        return false;
    }
}
