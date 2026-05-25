package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.Connection;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.TransportType;
import com.fouristhenumber.utilitiesinexcess.utils.MaskedArrayView;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;


public interface ITransferNetworkComponent
{
    void addNeighbor(ForgeDirection direction, ITransferNetworkComponent neighbor);
    void removeNeighbor(ForgeDirection direction);

    void addExternal(ForgeDirection direction, Connection neighbor);
    void removeExternal(ForgeDirection direction);

    Connection[] getValidExternalNeighbors(ForgeDirection fromDirection);
    ITransferNetworkComponent[] getNetworkNeighbors();

    MaskedArrayView<ITransferNetworkComponent> getWalkableDirs(TransportType targetType, ForgeDirection fromDirection);

    int getRawConnectionMask();

    World getWorld();

    int getX();
    int getY();
    int getZ();

    boolean canConnectToSide(ForgeDirection side);

    void updateExternalConnections();
}
