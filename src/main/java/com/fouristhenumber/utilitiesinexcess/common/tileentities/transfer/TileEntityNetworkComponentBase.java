package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.NetworkLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;


public abstract class TileEntityNetworkComponentBase<T extends NetworkLogic<? extends ITransferNetworkComponent>> extends TileEntity implements ITransferNetworkComponent
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
    public int getMeta()
    {
        return worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
    }
    @Override
    public void markHostDirty()
    {
        this.markDirty();
    }

    protected abstract T getLogic();
}
