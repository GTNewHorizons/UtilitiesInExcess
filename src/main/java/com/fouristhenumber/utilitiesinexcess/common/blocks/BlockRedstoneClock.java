package com.fouristhenumber.utilitiesinexcess.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityRedstoneClock;

public class BlockRedstoneClock extends BlockContainer {

    private IIcon iconInactive;
    private IIcon iconActive;

    public BlockRedstoneClock() {
        super(Material.rock);
        setBlockName("redstone_clock");
        setBlockTextureName("utilitiesinexcess:redstone_clock");
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        iconInactive = reg.registerIcon("utilitiesinexcess:redstone_clock_inactive");
        iconActive = reg.registerIcon("utilitiesinexcess:redstone_clock_active");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return meta == 1 ? iconActive : iconInactive;
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

        if (neighbor instanceof BlockRedstoneClock) {
            // Don't let neighbouring clocks trigger updates to each other
            return;
        }

        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityRedstoneClock) {
            ((TileEntityRedstoneClock) te).onInputChanged();
        }
    }
}
