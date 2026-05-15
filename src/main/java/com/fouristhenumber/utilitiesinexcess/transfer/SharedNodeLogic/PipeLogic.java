package com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic;


public class PipeLogic extends NetworkLogic
{

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
