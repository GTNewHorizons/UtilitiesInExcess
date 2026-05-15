package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import codechicken.lib.world.IChunkLoadTile;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.Connection;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.ITransferNetworkLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.TransportType;
import com.fouristhenumber.utilitiesinexcess.utils.MaskedArrayView;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;


public abstract class TileEntityNetworkComponentBase extends TileEntity implements ITransferNetworkComponent, IChunkLoadTile
{
    protected abstract ITransferNetworkLogic getNetworkLogic();

    /// See implemented class IChunkLoadTile for why we need to implement all of these.
    @Override
    public void invalidate()
    {
        super.invalidate();
        getNetworkLogic().separateWorld(this);
    }

    @Override
    public void onChunkUnload()
    {
        getNetworkLogic().separateWorld(this);
    }

    @Override
    public void validate()
    {
        super.validate();
        getNetworkLogic().tryJoinWorld(this);
    }

    @Override
    public void onChunkLoad() {
        getNetworkLogic().tryJoinWorld(this);
    }

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
    public void addNeighbor(ForgeDirection direction, ITransferNetworkComponent neighbor)
    {
        getNetworkLogic().addNeighbor(direction, neighbor);
    }

    @Override
    public void addExternal(ForgeDirection direction, Connection neighbor)
    {
        getNetworkLogic().addExternal(direction, neighbor);
    }

    @Override
    public void removeExternal(ForgeDirection direction)
    {
        getNetworkLogic().removeExternal(direction);
    }

    @Override
    public void removeNeighbor(ForgeDirection direction)
    {
        getNetworkLogic().removeNeighbor(direction);
    }

    @Override
    public void updateExternalConnections()
    {
        getNetworkLogic().updateExternalConnections(this);
    }

    @Override
    public Connection[] getExternalNeighbors()
    {
        return getNetworkLogic().getExternalConnections();
    }

    @Override
    public ITransferNetworkComponent[] getNetworkNeighbors()
    {
        return getNetworkLogic().getNetworkConnections();
    }

    // TODO make better
    @Override
    public MaskedArrayView<ITransferNetworkComponent> getWalkableDirs(TransportType targetType, ForgeDirection fromDirection)
    {
        return getNetworkLogic().getNeighborsExcluding(fromDirection);
    }

    @Override
    public int getRawConnectionMask()
    {
        ITransferNetworkLogic logic = getNetworkLogic();
        logic.updateExternalConnections(this);
        return logic.getNetworkMask() | logic.getExternalMask();
    }
}
