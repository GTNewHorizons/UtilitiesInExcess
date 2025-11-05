package com.fouristhenumber.utilitiesinexcess.common.blocks;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityCollector;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

public class BlockCollector extends BlockContainer {

    double x0, y0, z0, x1, y1, z1;


    public BlockCollector() {
        super(Material.rock);
        setBlockName("Collector");
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityCollector();
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
        if (!worldIn.isRemote) {
            TileEntity tile = worldIn.getTileEntity(x, y, z);
            if (tile instanceof TileEntityCollector collector) {
                collector.showBorderFor(100);
            }
        }
        return true;
    }
}
