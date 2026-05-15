package com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.utils.MaskedArrayView;
import net.minecraftforge.common.util.ForgeDirection;

public interface ITransferNetworkLogic
{
    void separateWorld(ITransferNetworkComponent host);

    void tryJoinWorld(ITransferNetworkComponent host);

    void removeNeighbor(ForgeDirection direction);
    void addNeighbor(ForgeDirection direction, ITransferNetworkComponent neighbor);

    MaskedArrayView<ITransferNetworkComponent> getNeighborsExcluding(ForgeDirection direction);

    int getNeighborMask();
}
