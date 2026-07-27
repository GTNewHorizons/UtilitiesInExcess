package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import cofh.api.energy.IEnergyHandler;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityTransferNodeBase;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.IWalkingComponent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

public abstract class BlockTransferBase extends BlockContainer
{
    protected BlockTransferBase(Material mat)
    {
        super(mat);
    }

    // Given that all directions have a pipe to go from, what directions can we walk from this network object.
    public abstract int validWalkDirections(World world, int x, int y, int z, ForgeDirection fromDirection, int metadata, IWalkingComponent<?> walkingComponent);

    // Gets the actual things that are connected used for rendering.
    public abstract int getConnectionMask(IBlockAccess world, int x, int y, int z, int metadata);

    // Given a block can it connect to the block from the given direction.
    public abstract boolean acceptsConnectionFrom(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection direction);

    public int maxInsertable(int metadata)
    {
        return -1;
    }

    public static boolean isValidConnectable(IBlockAccess world, int x, int y, int z, ForgeDirection dir)
    {
        boolean connects;
        if (world.getBlock(x, y, z) instanceof BlockTransferBase transferBase)
        {
            int meta = world.getBlockMetadata(x, y, z);
            connects = transferBase.acceptsConnectionFrom(world, x, y, z, meta, dir.getOpposite());
        }
        else
        {
            TileEntity te = world.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
            connects = te instanceof IFluidHandler || te instanceof IInventory || te instanceof IEnergyHandler;
        }
        return connects;
    }

}
