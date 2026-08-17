package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.BaseInserter;
import com.gtnewhorizons.angelica.shadow.javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

public interface IConnectable
{
    // Given that all directions have a pipe to go from, what directions can we walk from this network object.
    // Why do we have this on getConnectionMask? Because this function is walker context aware.
    int validWalkDirections(IBlockAccess world, int x, int y, int z, ForgeDirection fromDirection, IWalkingComponent<?> walkingComponent);

    static int validWalkDirections(List<IConnectable> connectables, IBlockAccess world, int x, int y, int z, ForgeDirection fromDirection, IWalkingComponent<?> walkingComponent)
    {
        int mask = 0b111111;
        for (IConnectable connectable : connectables)
        {
            mask &= connectable.validWalkDirections(world, x, y, z, fromDirection, walkingComponent);
        }
        return mask;
    }

    // Gets the actual things that are connected used for rendering.
    int getConnectionMask(IBlockAccess world, int x, int y, int z);

    // Static helper method for the multi connectable stuff
    static int getConnectionMask(List<IConnectable> connectables, IBlockAccess world, int x, int y, int z)
    {
        int mask = 0b111111;
        for (IConnectable connectable : connectables)
        {
            mask &= connectable.getConnectionMask(world, x, y, z);
        }
        return mask;
    }

    // Note that the following two methods are differing in that the getConnection function actually checks to make
    // sure the direction has a valid connectable. canConnectInDirection doesn't care if there is one or not.
    // Same as getConnectionMask, but for one direction. Implementations of getConnectionMask use this function.
    boolean getConnection(IBlockAccess world, int x, int y, int z, ForgeDirection dir);

    static boolean getConnection(List<IConnectable> connectables, IBlockAccess world, int x, int y, int z, ForgeDirection direction)
    {
        boolean hasconnection = true;
        for (IConnectable connectable : connectables)
        {
            hasconnection &= connectable.getConnection(world, x, y, z, direction);
        }
        return hasconnection;
    }

    // Given a block what are all possible directions that it can connect to, not caring what direction has a connectable
    boolean canConnectInDirection(IBlockAccess world, int x, int y, int z, ForgeDirection direction);

    static boolean canConnectInDirection(List<IConnectable> connectables, IBlockAccess world, int x, int y, int z, ForgeDirection direction)
    {
        boolean canConnect = true;
        for (IConnectable connectable : connectables)
        {
            canConnect &= connectable.canConnectInDirection(world, x, y, z, direction);
        }
        return canConnect;
    }

    BaseInserter getInserter(IBlockAccess world, int x, int y, int z);

    @Nullable
    static List<IConnectable> getConnectables(IBlockAccess world, int x, int y, int z)
    {
        List<IConnectable> connectables = new ArrayList<>();
        // Because of how the fake world works for ModelISBRH we have to do the multipart check first,
        // or it will defer the check to the block function for getConnection and ignore occluding
        // faces.
        if (Mods.ForgeMicroBlock.isLoaded())
        {
            if (world.getTileEntity(x, y, z) instanceof TileMultipart multipart)
            {
                for (TMultiPart part : multipart.jPartList())
                {
                    if (part instanceof IConnectable node)
                        connectables.add(node);
                }
            }
        }

        Block block = world.getBlock(x, y, z);
        if (block instanceof IConnectable node)
            connectables.add(node);
        return connectables;
    }
}
