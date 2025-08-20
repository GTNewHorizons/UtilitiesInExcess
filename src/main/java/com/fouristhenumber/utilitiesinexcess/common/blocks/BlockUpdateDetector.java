package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityBlockUpdateDetector();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (!world.isRemote) {

        }
        return true;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {

        if (neighbor instanceof BlockUpdateDetector) {
            // Don't let detectors trigger updates off each other
            return;
        }

        // Prevent constant updates from redstone dust (there must be a more correct way of doing this?)
        if (neighbor == Blocks.redstone_wire) {
            return;
        }

        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityBlockUpdateDetector) {
            if (((TileEntityBlockUpdateDetector) te).getOutputPower() == 0) {
                ((TileEntityBlockUpdateDetector) te).onNeighborUpdate();
            }
        }
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityBlockUpdateDetector) {
            return ((TileEntityBlockUpdateDetector) tileEntity).getOutputPower();
        }
        return 0;
    }

    @Override
    public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
        return true;
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
