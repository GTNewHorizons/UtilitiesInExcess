package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.UEMultipart;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.NetworkLogic;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public abstract class PartNetworkComponentBase<T extends NetworkLogic<? extends ITransferNetworkComponent>> extends UEMultipart implements ITransferNetworkComponent
{
    protected T logic;
    public int meta;

    protected PartNetworkComponentBase(int meta) {
        this.meta = meta;
    }

    @Override
    public World getWorld() {
        return world();
    }

    @Override
    public int getX() {
        return x();
    }

    @Override
    public int getY() {
        return y();
    }

    @Override
    public int getZ() {
        return z();
    }

    @Override
    public void markHostDirty() {
        tile().markDirty();
    }

    @Override
    public int getMeta()
    {
        return meta;
    }

    public abstract Block getBlock();

    protected abstract T getLogic();

}
