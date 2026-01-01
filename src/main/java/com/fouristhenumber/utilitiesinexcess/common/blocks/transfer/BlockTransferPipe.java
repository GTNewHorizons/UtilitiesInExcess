package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import static com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess.transferPipeRenderID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityTransferPipe;

public class BlockTransferPipe extends BlockContainer {

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
    public TileEntity createNewTileEntity(World world, int metadata) {
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

    protected void updateConnections(World world, int x, int y, int z) {
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
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        TileEntityTransferPipe te = (TileEntityTransferPipe) world.getTileEntity(x, y, z);
        if (te == null) return;
        int mask = te.getConnectionsMask();

        float minX = (mask & 1 << 1) != 0 ? 0F : 0.375F;
        float maxX = (mask & 1) != 0 ? 1F : 0.625F;
        float minY = (mask & 1 << 3) != 0 ? 0F : 0.375F;
        float maxY = (mask & 1 << 2) != 0 ? 1F : 0.625F;
        float minZ = (mask & 1 << 5) != 0 ? 0F : 0.375F;
        float maxZ = (mask & 1 << 4) != 0 ? 1F : 0.625F;
        this.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public int getRenderType() {
        return transferPipeRenderID;
    }
}
