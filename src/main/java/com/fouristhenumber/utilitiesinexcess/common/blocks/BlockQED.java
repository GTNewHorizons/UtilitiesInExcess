package com.fouristhenumber.utilitiesinexcess.common.blocks;

import static com.fouristhenumber.utilitiesinexcess.utils.UIEUtils.scanForBlock;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityQED;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

public class BlockQED extends BlockContainer {

    public BlockQED() {
        super(Material.iron);
        setBlockName("qed");
        setBlockTextureName("utilitiesinexcess:qed");
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityQED();
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        if (!worldIn.isRemote) {
            GuiFactories.tileEntity()
                .open(player, x, y, z);
        }
        return true;

    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        Set<BlockPos> positions = scanForBlock(world, x, y, z, 9, ModBlocks.FLUX_CRYSTAL.get());
        TileEntityQED qed = (TileEntityQED) world.getTileEntity(x, y, z);

        for (BlockPos pos : positions) {
            qed.addCrystal(new BlockPos(pos.x, pos.y, pos.z));
        }

        super.onBlockAdded(world, x, y, z);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block blockBroken, int meta) {
        Set<BlockPos> positions = scanForBlock(world, x, y, z, 9, ModBlocks.FLUX_CRYSTAL.get());
        TileEntityQED qed = (TileEntityQED) world.getTileEntity(x, y, z);

        for (BlockPos pos : positions) {
            qed.removeCrystal(new BlockPos(pos.x, pos.y, pos.z));
        }
        super.breakBlock(world, x, y, z, blockBroken, meta);
    }
}
