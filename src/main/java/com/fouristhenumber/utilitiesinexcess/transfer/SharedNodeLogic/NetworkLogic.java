package com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.RandomWalker;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.WalkerBase;
import com.fouristhenumber.utilitiesinexcess.utils.MaskedArrayView;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class NetworkLogic implements ITransferNetworkLogic
{
    private ITransferNetworkComponent[] connectedNeighbors = new ITransferNetworkComponent[6];
    private int neighborMask = 0;
    public WalkerBase walker;

    private boolean joined = false;
    public NetworkLogic(ITransferNetworkComponent logicHost)
    {
        walker = new RandomWalker(logicHost);
    }

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
    }

    @Override
    public void separateWorld(ITransferNetworkComponent host)
    {
        for(int i = 0; i < connectedNeighbors.length; i++)
        {
            if (connectedNeighbors[i] != null)
            {
                connectedNeighbors[i].removeNeighbor(ForgeDirection.getOrientation(i).getOpposite());
            }
        }
    }

    @Override
    public void removeNeighbor(ForgeDirection direction)
    {
        connectedNeighbors[direction.ordinal()] = null;
        neighborMask &= ~(1 << direction.ordinal());
    }

    @Override
    public void addNeighbor(ForgeDirection direction, ITransferNetworkComponent neighbor)
    {
        connectedNeighbors[direction.ordinal()] = neighbor;
        neighborMask |= (1 << direction.ordinal());
    }

    @Override
    public MaskedArrayView<ITransferNetworkComponent> getNeighborsExcluding(ForgeDirection direction)
    {
        int mask;
        if (direction != null)
        {
            mask = neighborMask & ~(1 << direction.ordinal());
        }
        else
        {
            mask = neighborMask;
        }
        return new MaskedArrayView<>(mask, connectedNeighbors);
    }

    @Override
    public int getNeighborMask() {
        return neighborMask;
    }
}
