package com.fouristhenumber.utilitiesinexcess.common.blocks;

import static com.fouristhenumber.utilitiesinexcess.utils.UIEUtils.scanForBlock;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityEnderLocus;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;

public class BlockConvergenceCrystal extends Block {

    public BlockConvergenceCrystal() {
        super(Material.iron);
        setBlockName("convergence_crystal");
        setBlockTextureName("utilitiesinexcess:convergence_crystal");
        setHardness(5.0F);
    }

    @Override
    public int getMobilityFlag() {
        return 2;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return ModelISBRH.JSON_ISBRH_ID;
    }

    @Override
    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ,
        int meta) {
        if (!worldIn.isRemote) {
            Set<BlockPos> positions = scanForBlock(worldIn, x, y, z, 9, ModBlocks.ENDER_LOCUS.get());
            for (BlockPos pos : positions) {
                TileEntityEnderLocus enderLocus = (TileEntityEnderLocus) worldIn.getTileEntity(pos.x, pos.y, pos.z);
                enderLocus.addCrystal();
            }
        }

        return super.onBlockPlaced(worldIn, x, y, z, side, subX, subY, subZ, meta);
    }

    @Override
    public void onBlockPreDestroy(World worldIn, int x, int y, int z, int meta) {
        if (!worldIn.isRemote) {
            Set<BlockPos> positions = scanForBlock(worldIn, x, y, z, 9, ModBlocks.ENDER_LOCUS.get());
            for (BlockPos pos : positions) {
                TileEntityEnderLocus enderLocus = (TileEntityEnderLocus) worldIn.getTileEntity(pos.x, pos.y, pos.z);
                enderLocus.removeCrystal();
            }
        }

        super.onBlockPreDestroy(worldIn, x, y, z, meta);
    }
}
