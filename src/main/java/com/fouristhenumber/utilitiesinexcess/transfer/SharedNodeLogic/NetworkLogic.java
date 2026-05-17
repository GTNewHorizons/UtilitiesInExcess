package com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic;

import cofh.api.energy.IEnergyReceiver;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.utils.MaskedArrayView;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

public abstract class NetworkLogic implements ITransferNetworkLogic
{
    private ITransferNetworkComponent[] networkNeighbors = new ITransferNetworkComponent[6];
    private int networkMask = 0;

    private Connection[] externalConnections = new Connection[6];
    private int externalConnectionMask;


    private boolean joined = false;
    public NetworkLogic()
    {}

    @Override
    public void tryJoinWorld(ITransferNetworkComponent host)
    {
        World world = host.getWorld();
        int x = host.getX();
        int y = host.getY();
        int z = host.getZ();
        if (!joined && world != null && world.blockExists(x, y, z))
        {
            joined = true;
            for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
            {
                int xOffset = x + dir.offsetX;
                int yOffset = y + dir.offsetY;
                int zOffset = z + dir.offsetZ;
                if (world.blockExists(xOffset, yOffset, zOffset) &&
                    world.getChunkFromBlockCoords(xOffset, zOffset).getTileEntityUnsafe(xOffset & 15, yOffset, zOffset & 15)
                        instanceof ITransferNetworkComponent component)
                {
                    if (component.canConnectToSide(dir.getOpposite()))
                    {
                        component.addNeighbor(dir.getOpposite(), host);
                        addNeighbor(dir, component);
                    }
                }
            }
        }

        updateExternalConnections(host);
    }

    public void updateExternalConnections(ITransferNetworkComponent host)
    {
        for (int i = 0; i < 6; i++) {
            ForgeDirection dir = ForgeDirection.getOrientation(i);
            int xOffset = host.getX() + dir.offsetX;
            int yOffset = host.getY() + dir.offsetY;
            int zOffset = host.getZ() + dir.offsetZ;
            World world = host.getWorld();

            if (world.blockExists(xOffset, yOffset, zOffset) && host.canConnectToSide(dir)) {


                TileEntity neighborTE = world.getChunkFromBlockCoords(xOffset, zOffset).getTileEntityUnsafe(xOffset & 15, yOffset, zOffset & 15);

                if (neighborTE instanceof ITransferNetworkComponent) {
                    continue;
                }

                // Just update the externals because it's way more painful to check if everything is the same.
                // Just assume we're just now getting placed.
                boolean setExternal = false;
                if (neighborTE != null) {
                    int flags = 0;
                    if (neighborTE instanceof IInventory) {
                        flags |= 1;
                        setExternal = true;
                    }
                    if (neighborTE instanceof IFluidHandler) {
                        flags |= 2;
                        setExternal = true;
                    }
                    if (neighborTE instanceof IEnergyReceiver) {
                        flags |= 4;
                        setExternal = true;
                    }
                    if (setExternal) {
                        host.addExternal(dir, new Connection(neighborTE, flags, i));
                    }
                }

                if (!setExternal) {
                    host.removeExternal(dir);
                }
            }
        }
    }


    @Override
    public void separateWorld(ITransferNetworkComponent host)
    {
        for(int i = 0; i < networkNeighbors.length; i++)
        {
            if (networkNeighbors[i] != null)
            {
                networkNeighbors[i].removeNeighbor(ForgeDirection.getOrientation(i).getOpposite());
            }
        }
    }

    @Override
    public void addNeighbor(ForgeDirection direction, ITransferNetworkComponent neighbor)
    {
        networkNeighbors[direction.ordinal()] = neighbor;
        networkMask |= (1 << direction.ordinal());
    }

    @Override
    public void removeNeighbor(ForgeDirection direction)
    {
        networkNeighbors[direction.ordinal()] = null;
        networkMask &= ~(1 << direction.ordinal());
    }

    @Override
    public void addExternal(ForgeDirection direction, Connection neighbor)
    {
        externalConnections[direction.ordinal()] = neighbor;
        externalConnectionMask |= (1 << direction.ordinal());
    }

    @Override
    public void removeExternal(ForgeDirection direction)
    {
        externalConnections[direction.ordinal()] = null;
        externalConnectionMask &= ~(1 << direction.ordinal());
    }

    @Override
    public MaskedArrayView<ITransferNetworkComponent> getNeighborsExcluding(ForgeDirection direction)
    {
        int mask;
        if (direction != null)
        {
            mask = networkMask & ~(1 << direction.ordinal());
        }
        else
        {
            mask = networkMask;
        }
        return new MaskedArrayView<>(mask, networkNeighbors);
    }

    @Override
    public int getNetworkMask() {
        return networkMask;
    }

    @Override
    public int getExternalMask() {
        return externalConnectionMask;
    }

    @Override
    public void setNetworkMask(int mask)
    {
        networkMask = mask;
    }

    @Override
    public void setExternalMask(int mask)
    {
        externalConnectionMask = mask;
    }

    @Override
    public Connection[] getExternalConnections()
    {
        return externalConnections;
    }

    @Override
    public ITransferNetworkComponent[] getNetworkConnections()
    {
        return networkNeighbors;
    }
}
