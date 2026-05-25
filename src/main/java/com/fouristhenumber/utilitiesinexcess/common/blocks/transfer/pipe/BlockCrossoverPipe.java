package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.pipe;

import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockTransferBase;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe.TileEntityCrossoverPipe;
import com.fouristhenumber.utilitiesinexcess.transfer.collision.PipeCollision;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

import static com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess.transferPipeRenderID;

public class BlockCrossoverPipe extends BlockTransferBase
{
    public BlockCrossoverPipe() {
        super(Material.iron);
        this.setBlockName("crossover_pipe");
        this.setBlockTextureName("utilitiesinexcess:transfer_pipe");
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityCrossoverPipe();
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean hasTileEntity(int metadata)
    {
        return true;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
        TileEntityCrossoverPipe te = (TileEntityCrossoverPipe) world.getTileEntity(x, y, z);
        if (te == null) return;

        int mask = te.getRawConnectionMask();

        float minY = (mask & (1 << 0)) != 0 ? 0F : 0.375F; // DOWN (-Y)
        float maxY = (mask & (1 << 1)) != 0 ? 1F : 0.625F; // UP (+Y)

        float minZ = (mask & (1 << 2)) != 0 ? 0F : 0.375F; // NORTH (-Z)
        float maxZ = (mask & (1 << 3)) != 0 ? 1F : 0.625F; // SOUTH (+Z)

        float minX = (mask & (1 << 4)) != 0 ? 0F : 0.375F; // WEST (-X)
        float maxX = (mask & (1 << 5)) != 0 ? 1F : 0.625F; // EAST (+X)

        this.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collider)
    {
        TileEntityCrossoverPipe te = (TileEntityCrossoverPipe) worldIn.getTileEntity(x, y, z);
        if (te == null) return;

        int connectionMask = te.getRawConnectionMask();

        AxisAlignedBB boundingBox = PipeCollision.MIDDLE.getBoundingBox().copy().offset(x, y, z);
        if (boundingBox.intersectsWith(mask))
        {
            list.add(boundingBox);
        }

        boundingBox = PipeCollision.DOWN.getBoundingBox().copy().offset(x, y, z);
        if ((connectionMask & (1 << 0)) != 0 && boundingBox.intersectsWith(mask))
        {
            list.add(boundingBox);
        }

        boundingBox = PipeCollision.UP.getBoundingBox().copy().offset(x, y, z);
        if ((connectionMask & (1 << 1)) != 0 && boundingBox.intersectsWith(mask))
        {
            list.add(boundingBox);
        }

        boundingBox = PipeCollision.NORTH.getBoundingBox().copy().offset(x, y, z);
        if ((connectionMask & (1 << 2)) != 0 && boundingBox.intersectsWith(mask))
        {
            list.add(boundingBox);
        }

        boundingBox = PipeCollision.SOUTH.getBoundingBox().copy().offset(x, y, z);
        if ((connectionMask & (1 << 3)) != 0 && boundingBox.intersectsWith(mask))
        {
            list.add(boundingBox);
        }

        boundingBox = PipeCollision.WEST.getBoundingBox().copy().offset(x, y, z);
        if ((connectionMask & (1 << 4)) != 0 && boundingBox.intersectsWith(mask))
        {
            list.add(boundingBox);
        }

        boundingBox = PipeCollision.EAST.getBoundingBox().copy().offset(x, y, z);
        if ((connectionMask & (1 << 5)) != 0 && boundingBox.intersectsWith(mask))
        {
            list.add(boundingBox);
        }
    }

    @Override
    public int getRenderType()
    {
        return transferPipeRenderID;
    }
}
