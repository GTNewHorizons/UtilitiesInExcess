package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.pipe;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe.TileEntitySortingPipe;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockSortingPipe extends BlockPipeBase
{
    public BlockSortingPipe() {
        super(Material.iron);
        this.setBlockName("sorting_pipe");
        this.setBlockTextureName("utilitiesinexcess:sorting_pipe");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntitySortingPipe();
    }
}
