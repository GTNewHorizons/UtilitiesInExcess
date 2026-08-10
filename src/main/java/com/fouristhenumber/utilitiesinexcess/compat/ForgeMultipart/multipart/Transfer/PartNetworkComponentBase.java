package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.ConnectablePart;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.NetworkLogic;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public abstract class PartNetworkComponentBase<T extends NetworkLogic<? extends ITransferNetworkComponent>> extends ConnectablePart implements ITransferNetworkComponent
{
    protected T logic;
    public byte meta;

    protected PartNetworkComponentBase(int side) {
        super(side);
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

    public abstract Block getBlock();

    protected abstract T getLogic();

}
