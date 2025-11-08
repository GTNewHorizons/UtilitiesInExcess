package com.fouristhenumber.utilitiesinexcess.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityCollector;

public class BlockCollector extends BlockContainer {

    public BlockCollector() {
        super(Material.rock);
        setBlockName("Collector");
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityCollector();
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        TileEntity tile = worldIn.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityCollector collector)) {
            return true;
        }


        if(collector.getTimeSinceLastClick() < 40 && collector.getTimeSinceLastClick() > 0 ) {
            collector.incrementSize(player);
            collector.setTimeSinceLastClick(0);

        }

        collector.showBorderFor(40);
        worldIn.markBlockForUpdate(x, y, z);
        return true;
    }
}
