package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityBlockUpdateDetector;

public class BlockUpdateDetector extends BlockContainer {

    public BlockUpdateDetector() {
        super(Material.rock);
        setBlockName("block_update_detector");
        setBlockTextureName("utilitiesinexcess:block_update_detector");
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        super.onNeighborBlockChange(world, x, y, z, neighbor);

        if(neighbor instanceof BlockUpdateDetector) {
            // Don't let update detectors trigger each other
            return;
        }

        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityBlockUpdateDetector) {
            ((TileEntityBlockUpdateDetector) te).onNeighborUpdate();
        }
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityBlockUpdateDetector) {
            return ((TileEntityBlockUpdateDetector) te).getOutputPower();
        }
        return 0;
    }

    @Override
    public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityBlockUpdateDetector();
    }

    public static class ItemBlockUpdateDetector extends ItemBlock {

        public ItemBlockUpdateDetector(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean bool) {
            tooltip.add(StatCollector.translateToLocal("tile.block_update_detector.desc"));
        }
    }
}
