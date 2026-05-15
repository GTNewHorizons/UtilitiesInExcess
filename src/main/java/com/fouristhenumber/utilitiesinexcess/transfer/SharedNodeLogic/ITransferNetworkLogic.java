package com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.utils.MaskedArrayView;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public interface ITransferNetworkLogic
{
    void separateWorld(ITransferNetworkComponent host);

    void tryJoinWorld(ITransferNetworkComponent host);
    void updateExternalConnections(ITransferNetworkComponent host);

    void addExternal(ForgeDirection direction, Connection neighbor);
    void removeExternal(ForgeDirection direction);

    void removeNeighbor(ForgeDirection direction);
    void addNeighbor(ForgeDirection direction, ITransferNetworkComponent neighbor);

    MaskedArrayView<ITransferNetworkComponent> getNeighborsExcluding(ForgeDirection direction);

    int getNetworkMask();
    int getExternalMask();
    void setNetworkMask(int mask);
    void setExternalMask(int mask);

    boolean canConnectEnergy();
    boolean canConnectFluid();
    boolean canConnectItem();

    Connection[] getExternalConnections();
    ITransferNetworkComponent[] getNetworkConnections();


}
