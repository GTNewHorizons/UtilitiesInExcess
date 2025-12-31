package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import static com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess.transferPipeRenderID;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityTransferPipe;

public class BlockTransferPipe extends Block {

    public BlockTransferPipe() {
        super(Material.iron);
        this.setBlockName("transfer_pipe");
        this.setBlockTextureName("utilitiesinexcess:transfer_pipe");
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileEntityTransferPipe();
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        updateConnections(world, x, y, z);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        updateConnections(world, x, y, z);
    }

    private void updateConnections(World world, int x, int y, int z) {
        if (!world.isRemote) {
            TileEntityTransferPipe te = (TileEntityTransferPipe) world.getTileEntity(x, y, z);
            if (te != null) {
                boolean changed = te.updateConnections(world, x, y, z);
                if (changed) {
                    world.markBlockForUpdate(x, y, z);
                }
            }
        }
    }

    @Override
    public int getRenderType() {
        return transferPipeRenderID;
    }
}
