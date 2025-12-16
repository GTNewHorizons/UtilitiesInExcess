package com.fouristhenumber.utilitiesinexcess.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityAdvancedBlockUpdateDetector;

public class BlockAdvancedUpdateDetector extends BlockContainer {

    public BlockAdvancedUpdateDetector() {
        super(Material.rock);
        setBlockName("advanced_block_update_detector");
        setBlockTextureName("utilitiesinexcess:advanced_block_update_detector");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityAdvancedBlockUpdateDetector();
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityAdvancedBlockUpdateDetector tileBUD) {
            return tileBUD.getOutputPower();
        }
        return 0;
    }

    @Override
    public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
        return true;
    }
}
