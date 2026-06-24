package com.fouristhenumber.utilitiesinexcess.common.blocks;

import static com.fouristhenumber.utilitiesinexcess.utils.UIEUtils.scanForBlock;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityQED;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

public class BlockFluxCrystal extends Block {

    public BlockFluxCrystal() {
        super(Material.iron);
        setBlockName("flux_crystal");
        setBlockTextureName("utilitiesinexcess:flux_crystal");
        setHardness(5.0F);
    }

    @Override
    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ,
        int meta) {
        if (!worldIn.isRemote) {
            Set<BlockPos> positions = scanForBlock(worldIn, x, y, z, 9, ModBlocks.QED.get());
            for (BlockPos pos : positions) {
                TileEntityQED qed = (TileEntityQED) worldIn.getTileEntity(pos.x, pos.y, pos.z);
                qed.addCrystal();
            }
        }

        return super.onBlockPlaced(worldIn, x, y, z, side, subX, subY, subZ, meta);
    }

    @Override
    public void onBlockPreDestroy(World worldIn, int x, int y, int z, int meta) {
        if (!worldIn.isRemote) {
            Set<BlockPos> positions = scanForBlock(worldIn, x, y, z, 9, ModBlocks.QED.get());
            for (BlockPos pos : positions) {
                TileEntityQED qed = (TileEntityQED) worldIn.getTileEntity(pos.x, pos.y, pos.z);
                qed.removeCrystal();
            }
        }

        super.onBlockPreDestroy(worldIn, x, y, z, meta);
    }
}
