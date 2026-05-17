package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.ITransferNetworkComponent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class BlockTransferBase extends BlockContainer
{
    protected BlockTransferBase(Material mat)
    {
        super(mat);
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        TileEntity te = worldIn.getTileEntity(x, y, z);

        if (!(te instanceof ITransferNetworkComponent component))
        {
            return;
        }
        component.updateExternalConnections();
    }
}
