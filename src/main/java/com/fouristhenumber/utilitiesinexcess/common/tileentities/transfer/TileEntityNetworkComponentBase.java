package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;


public abstract class TileEntityNetworkComponentBase<T> extends TileEntity implements ITransferNetworkComponent
{
    protected T logic;

    @Override
    public World getWorld() {
        return this.worldObj;
    }

    @Override
    public int getX() {
        return this.xCoord;
    }

    @Override
    public int getY() {
        return this.yCoord;
    }

    @Override
    public int getZ() {
        return this.zCoord;
    }

    @Override
    public void markHostDirty()
    {
        this.markDirty();
    }
}
