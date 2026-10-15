package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.EnergyTransferNodeLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.NetworkLogic;

public class TileEntityEnergyTransferNode extends TileEntityTransferNodeBase implements IWirelessNode {

    @Override
    public NetworkLogic getLogic() {
        if (logic == null) {
            logic = new EnergyTransferNodeLogic(this);
        }
        return logic;
    }

    public void invalidateInventory() {
        getLogic().invalidateInventory();
    }

    @Override
    public int receiveEnergy(ForgeDirection forgeDirection, int i, boolean b) {
        return ((EnergyTransferNodeLogic) getLogic()).receiveEnergy(forgeDirection, i, b);
    }

    @Override
    public int extractEnergy(ForgeDirection forgeDirection, int i, boolean b) {
        return ((EnergyTransferNodeLogic) getLogic()).extractEnergy(forgeDirection, i, b);
    }

    @Override
    public int getEnergyStored(ForgeDirection forgeDirection) {
        return ((EnergyTransferNodeLogic) getLogic()).getEnergyStored(forgeDirection);
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection forgeDirection) {
        return ((EnergyTransferNodeLogic) getLogic()).getMaxEnergyStored(forgeDirection);
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection forgeDirection) {
        return ((EnergyTransferNodeLogic) getLogic()).canConnectEnergy(forgeDirection);
    }

    @Override
    public ForgeDirection getFacing() {
        return ForgeDirection.UNKNOWN;
    }
}
