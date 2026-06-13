package com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.pipe;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.Connection;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.NetworkLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.TransportType;
import com.fouristhenumber.utilitiesinexcess.utils.MaskedArrayView;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

// Crossover pipes are unique in that they are the only pipe that when they are updated they will affect neighbor status
// of blocks that are not directly adjacent, thus they must have special neighbor logic.
public class CrossOverPipeLogic extends NetworkLogic
{
    public CrossOverPipeLogic(ITransferNetworkComponent host) {
        super(host);
    }

    @Override
    public boolean canConnectEnergy() {
        return true;
    }

    @Override
    public boolean canConnectFluid() {
        return true;
    }

    @Override
    public boolean canConnectItem() {
        return true;
    }

    @Override
    public void addNeighbor(ForgeDirection dir, ITransferNetworkComponent neighbor)
    {
        ForgeDirection opp = dir.getOpposite();

        // Add our original neighbor
        networkNeighbors[dir.ordinal()] = neighbor;
        networkMask |= (1 << dir.ordinal());

        World world = host.getWorld();

        // Gets the block on the opposite side of the crossover pipe.
        int x = host.getX();
        int y = host.getY();
        int z = host.getZ();

        int xOffset = x + opp.offsetX;
        int yOffset = y + opp.offsetY;
        int zOffset = z + opp.offsetZ;

        // Since we know that the only time this is called is when the opposite item is valid to connect too.
        // So we should add that item.
        // What we don't know is if that item is a network connection or an external connection. So we need to check for it.
        if (world.blockExists(xOffset, yOffset, zOffset))
        {
            TileEntity candidate = world.getChunkFromBlockCoords(xOffset, zOffset).getTileEntityUnsafe(xOffset & 15, yOffset, zOffset & 15);

            if (candidate instanceof ITransferNetworkComponent component)
            {
                if (component.canConnectToSide(dir))
                {
                    component.addNeighbor(dir, host);
                    networkNeighbors[opp.ordinal()] = component;
                    networkMask |= (1 << opp.ordinal());
                }
            }
            else
            {
                int connectionFlags = resolveExternalConnection(candidate);
                if (connectionFlags != 0) // Pretty sure this should never happen but might as well check.
                {
                    externalConnections[opp.ordinal()] = new Connection(candidate, connectionFlags, opp.ordinal());
                    externalConnectionMask |= (1 << opp.ordinal());
                }
            }
        }
        world.markBlockForUpdate(x, y, z);
    }

    @Override
    public void removeNeighbor(ForgeDirection dir)
    {
        ForgeDirection opp = dir.getOpposite();

        networkNeighbors[dir.ordinal()] = null;
        networkMask &= ~(1 << dir.ordinal());

        World world = host.getWorld();

        // Gets the block on the opposite side of the crossover pipe.
        int x = host.getX();
        int y = host.getY();
        int z = host.getZ();

        int xOffset = x + opp.offsetX;
        int yOffset = y + opp.offsetY;
        int zOffset = z + opp.offsetZ;

        // Since we know that the only time this is called is when the opposite neighbor is no longer valid to connect to
        // So we should remove that
        if (world.blockExists(xOffset, yOffset, zOffset) &&
            world.getChunkFromBlockCoords(xOffset, zOffset).getTileEntityUnsafe(xOffset & 15, yOffset, zOffset & 15)
                instanceof ITransferNetworkComponent component)
        {
            component.removeNeighbor(dir);
            networkNeighbors[opp.ordinal()] = null;
            networkMask &= ~(1 << opp.ordinal());
        }
        else // We can just blindly remove this because even if it's there (which it should certainly be) It needs to be null no matter what.
        {
            externalConnections[opp.ordinal()] = null;
            externalConnectionMask &= ~(1 << opp.ordinal());
        }
        world.markBlockForUpdate(x, y, z);
    }

    @Override
    public void addExternal(ForgeDirection dir, Connection neighbor)
    {
        ForgeDirection opp = dir.getOpposite();

        // Gets the block on the opposite side of the crossover pipe.

        World world = host.getWorld();

        int x = host.getX();
        int y = host.getY();
        int z = host.getZ();

        int xOffset = x + opp.offsetX;
        int yOffset = y + opp.offsetY;
        int zOffset = z + opp.offsetZ;

        // Same deal as with network neighbors. We need to check if there's an opposing network neighbor that needs to be added (which there should be)
        // when we add this external connection.
        // I see no reason to support external to external connections with a crossover pipe.
        if (world.blockExists(xOffset, yOffset, zOffset))
        {
            TileEntity candidate = world.getChunkFromBlockCoords(xOffset, zOffset).getTileEntityUnsafe(xOffset & 15, yOffset, zOffset & 15);
            if (candidate instanceof ITransferNetworkComponent component)
            {
                if (component.canConnectToSide(dir) && host.canConnectToSide(opp))
                {
                    component.addNeighbor(dir, host);

                    // Only add the connection if the connection is not external to external
                    externalConnections[dir.ordinal()] = neighbor;
                    externalConnectionMask |= (1 << dir.ordinal());

                    networkNeighbors[opp.ordinal()] = component;
                    networkMask |= (1 << opp.ordinal());
                }
            }
        }
        host.getWorld().markBlockForUpdate(host.getX(), host.getY(), host.getZ());
    }

    @Override
    public void removeExternal(ForgeDirection dir)
    {
        ForgeDirection opp = dir.getOpposite();

        // Same deal as with network neighbors. We need to check if there's an opposing network neighbor that needs to be removed
        // when we remove this external connection
        externalConnections[dir.ordinal()] = null;
        externalConnectionMask &= ~(1 << dir.ordinal());

        World world = host.getWorld();

        // Gets the block on the opposite side of the crossover pipe.
        int x = host.getX();
        int y = host.getY();
        int z = host.getZ();

        int xOffset = x + opp.offsetX;
        int yOffset = y + opp.offsetY;
        int zOffset = z + opp.offsetZ;

        if (world.blockExists(xOffset, yOffset, zOffset) &&
            world.getChunkFromBlockCoords(xOffset, zOffset).getTileEntityUnsafe(xOffset & 15, yOffset, zOffset & 15)
                instanceof ITransferNetworkComponent component)
        {
            component.removeNeighbor(dir);
            networkNeighbors[opp.ordinal()] = null;
            networkMask &= ~(1 << opp.ordinal());
        }

        host.getWorld().markBlockForUpdate(host.getX(), host.getY(), host.getZ());
    }

    // Only valid walkable neighbor is across from the fromDirection
    @Override
    public MaskedArrayView<ITransferNetworkComponent> getWalkableDirs(TransportType transportType, ForgeDirection incomingDirection, IWalkingComponent<?> walkingComponent)
    {
        int mask = 0;
        mask |= (1 << incomingDirection.getOpposite().ordinal());
        return new MaskedArrayView<>(mask, networkNeighbors);
    }

    // Only valid external connection is across from the fromDirection
    @Override
    public Connection[] getValidExternalConnections(ForgeDirection fromDirection, IWalkingComponent walker)
    {
        return new Connection[]{externalConnections[fromDirection.getOpposite().ordinal()]};
    }

}
