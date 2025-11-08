package com.fouristhenumber.utilitiesinexcess.common.blocks;

import static codechicken.nei.NEIClientConfig.world;
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
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        Set<BlockPos> positions = scanForBlock(world, x, y, z, 9, ModBlocks.QED.get());

        for (BlockPos pos : positions) {
            TileEntityQED qed = (TileEntityQED) world.getTileEntity(pos.x, pos.y, pos.z);
            qed.addCrystal(new BlockPos(x, y, z));
        }
        super.onBlockAdded(world, x, y, z);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block blockBroken, int meta) {
        Set<BlockPos> positions = scanForBlock(world, x, y, z, 9, ModBlocks.QED.get());

        for (BlockPos pos : positions) {
            TileEntityQED qed = (TileEntityQED) world.getTileEntity(pos.x, pos.y, pos.z);
            qed.removeCrystal(new BlockPos(x, y, z));
        }
        super.breakBlock(world, x, y, z, blockBroken, meta);
    }
}
