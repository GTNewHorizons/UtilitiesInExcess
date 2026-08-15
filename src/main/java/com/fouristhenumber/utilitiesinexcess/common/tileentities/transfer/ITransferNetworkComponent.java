package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import net.minecraft.world.World;

public interface ITransferNetworkComponent
{
    World getWorld();

    int getX();
    int getY();
    int getZ();

    int getMeta();

    void markHostDirty();
}
