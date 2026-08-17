package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.BaseInserter;
import com.gtnewhorizons.angelica.shadow.javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public interface IConnectable
{
    // Given that all directions have a pipe to go from, what directions can we walk from this network object.
    int validWalkDirections(World world, int x, int y, int z, ForgeDirection fromDirection, IWalkingComponent<?> walkingComponent);

    // Gets the actual things that are connected used for rendering.
    int getConnectionMask(IBlockAccess world, int x, int y, int z);

    // Same as getConnectionMask, but for one direction. Implementations of getConnectionMask use this function.
    // In theory it oculd
    boolean getConnection(IBlockAccess world, int x, int y, int z, ForgeDirection dir);

    // Given a block what are all possible directions that it can connect to, not caring what direction has a connectable
    boolean canConnectInDirection(IBlockAccess world, int x, int y, int z, ForgeDirection direction);

    BaseInserter getInserter(IBlockAccess world, int x, int y, int z);

    @Nullable
    static IConnectable getConnectable(IBlockAccess world, int x, int y, int z)
    {
        Block block = world.getBlock(x, y, z);

        if (block instanceof IConnectable node)
            return node;

        if (Mods.ForgeMicroBlock.isLoaded())
        {
            if (world.getTileEntity(x, y, z) instanceof TileMultipart multipart)
            {
                for (TMultiPart part : multipart.jPartList())
                {
                    if (part instanceof IConnectable node)
                        return node;
                }
            }
        }

        return null;
    }
}
