package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;


import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityNetworkComponentBase;
import com.fouristhenumber.utilitiesinexcess.transfer.collision.PipeCollision;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.cleanroommc.modularui.factory.GuiFactories;

import java.util.List;

public abstract class BlockNodeBase extends BlockTransferBase
{


    protected BlockNodeBase() {
        super(Material.iron);
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
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        if (!worldIn.isRemote) GuiFactories.tileEntity()
            .open(player, x, y, z);
        return true;
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z,
                             int side, float hitX, float hitY, float hitZ,
                             int meta)
    {
        return (meta << 3) | side;
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collider)
    {
        TileEntityNetworkComponentBase<?> te = (TileEntityNetworkComponentBase<?>) worldIn.getTileEntity(x, y, z);
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

    public abstract int getRenderType();
}
