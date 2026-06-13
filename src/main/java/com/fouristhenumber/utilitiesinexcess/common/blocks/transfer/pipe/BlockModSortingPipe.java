package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.pipe;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe.TileEntityModSortingPipe;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockModSortingPipe extends BlockPipeBase
{
    public BlockModSortingPipe() {
        super(Material.iron);
        this.setBlockName("hyper_rationing_pipe");
        this.setBlockTextureName("utilitiesinexcess:hyper_rationing_pipe");
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityModSortingPipe();
    }
}
