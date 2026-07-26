package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityTransferNodeBase;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.IWalkingComponent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class BlockTransferBase extends BlockContainer
{
    protected BlockTransferBase(Material mat)
    {
        super(mat);
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        // TODO probably just update rendering.
    }

    public abstract int validWalkDirections(World world, int x, int y, int z, ForgeDirection fromDirection, int metadata, IWalkingComponent<?> walkingComponent);
    public abstract int getConnectionMask(IBlockAccess world, int x, int y, int z, int metadata);
    public boolean acceptsConnectionFrom(ForgeDirection direction)
    {
        return true;
    }
}
