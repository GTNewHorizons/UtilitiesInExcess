package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.NetworkLogic;

public abstract class TileEntityNetworkComponentBase extends TileEntity implements ITransferNetworkComponent {

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
    public int getMeta() {
        return worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
    }

    @Override
    public void markHostDirty() {
        this.markDirty();
    }

    public abstract void invalidateInventory();

    public abstract NetworkLogic getLogic();
}
