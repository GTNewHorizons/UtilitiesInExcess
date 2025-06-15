package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;

public class TileEntityRainMuffler extends TileEntitySoundMuffler {

    @Override
    public void enableMuffler() {
        UtilitiesInExcess.proxy.soundEventHandler.putRainMuffler(worldObj.provider.dimensionId, xCoord, yCoord, zCoord);
    }

    @Override
    public void disableMuffler() {
        UtilitiesInExcess.proxy.soundEventHandler
            .removeRainMuffler(worldObj.provider.dimensionId, xCoord, yCoord, zCoord);
    }
}
