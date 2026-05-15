package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import com.fouristhenumber.utilitiesinexcess.utils.MaskedArrayView;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;


public interface ITransferNetworkComponent
{
    void addNeighbor(ForgeDirection direction, ITransferNetworkComponent neighbor);
    void removeNeighbor(ForgeDirection direction);

    List<ITransferNetworkComponent> getNeighbors();

    // We need to have the walking component because it's likely that this will be relevant to the walkability
    MaskedArrayView<ITransferNetworkComponent> getWalkableDirs(ITransferNetworkComponent walkingComponent, ForgeDirection fromDirection);

    int getRawConnectionMask();

    World getWorld();

    int getX();
    int getY();
    int getZ();

    boolean canConnectToSide(ForgeDirection side);
}
