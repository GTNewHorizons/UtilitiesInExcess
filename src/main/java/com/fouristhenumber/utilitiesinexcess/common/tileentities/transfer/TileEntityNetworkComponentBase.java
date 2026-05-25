package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import codechicken.lib.world.IChunkLoadTile;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.Connection;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.ITransferNetworkLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.TransportType;
import com.fouristhenumber.utilitiesinexcess.utils.MaskedArrayView;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;


public abstract class TileEntityNetworkComponentBase<T extends ITransferNetworkLogic> extends TileEntity implements ITransferNetworkComponent, IChunkLoadTile
{
    protected T logic;

    /// See implemented class IChunkLoadTile for why we need to implement all of these.
    @Override
    public void invalidate()
    {
        super.invalidate();
        logic.separateWorld();
    }

    @Override
    public void onChunkUnload()
    {
        logic.separateWorld();
    }

    @Override
    public void validate()
    {
        super.validate();
        logic.tryJoinWorld();
    }

    @Override
    public void onChunkLoad() {
        logic.tryJoinWorld();
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
        logic.addNeighbor(direction, neighbor);
    }

    @Override
    public void addExternal(ForgeDirection direction, Connection neighbor)
    {
        logic.addExternal(direction, neighbor);
    }

    @Override
    public void removeExternal(ForgeDirection direction)
    {
        logic.removeExternal(direction);
    }

    @Override
    public void removeNeighbor(ForgeDirection direction)
    {
        logic.removeNeighbor(direction);
    }

    @Override
    public void updateExternalConnections()
    {
        logic.updateExternalConnections();
    }

    @Override
    public Connection[] getValidExternalNeighbors(ForgeDirection fromDirection)
    {
        return logic.getValidExternalConnections(fromDirection);
    }

    @Override
    public ITransferNetworkComponent[] getNetworkNeighbors()
    {
        return logic.getNetworkConnections();
    }

    // TODO make better
    @Override
    public MaskedArrayView<ITransferNetworkComponent> getWalkableDirs(TransportType targetType, ForgeDirection fromDirection)
    {
        return logic.getWalkableDirs(fromDirection);
    }

    @Override
    public int getRawConnectionMask()
    {
        logic.updateExternalConnections();
        return logic.getNetworkMask() | logic.getExternalMask();
    }
}
