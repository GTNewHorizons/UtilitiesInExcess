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

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityRedstoneClock;

public class BlockRedstoneClock extends BlockContainer {

    public BlockRedstoneClock() {
        super(Material.rock);
        setBlockName("redstone_clock");
        setBlockTextureName("utilitiesinexcess:redstone_clock");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityRedstoneClock();
    }

    @Override
    public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityRedstoneClock) {
            return ((TileEntityRedstoneClock) te).getOutputPower();
        }
        return 0;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        super.onNeighborBlockChange(world, x, y, z, neighbor);
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityRedstoneClock) {
            ((TileEntityRedstoneClock) te).onInputChanged();
        }
    }

    public static class ItemBlockRedstoneClock extends ItemBlock {

        public ItemBlockRedstoneClock(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean bool) {
            tooltip.add(StatCollector.translateToLocal("tile.redstoneClock.desc.1"));
            tooltip.add(StatCollector.translateToLocal("tile.redstoneClock.desc.2"));
        }
    }
}
