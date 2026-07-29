package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;


import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityNetworkComponentBase;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.collision.PipeCollision;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.cleanroommc.modularui.factory.GuiFactories;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

import static com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess.flatNodeRenderID;

public abstract class BlockNodeBase extends BlockTransferBase
{
    protected BlockNodeBase() {
        super(Material.iron);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side)
    {
        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
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
        return (meta << 3) | ForgeDirection.getOrientation(side).getOpposite().ordinal();
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collider)
    {
        Block block = worldIn.getBlock(x, y, z);
        if (block instanceof BlockTransferBase transferBase)
        {
            int connectionMask = transferBase.getConnectionMask(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z));

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

    }

    @Override
    public boolean hasTileEntity(int metadata)
    {
        return true;
    }

    @Override
    public int validWalkDirections(World world, int x, int y, int z, ForgeDirection fromDirection, int metadata, IWalkingComponent<?> walkingComponent)
    {
        int mask = 0b111111;
        int facing = metadata & 7;
        if (facing < 6)
        {
            mask &= ~(1 << facing);
        }
        if (fromDirection != ForgeDirection.UNKNOWN)
        {
            mask &= ~(1 << fromDirection.ordinal());
        }
        return mask;
    }

    @Override
    public int getConnectionMask(IBlockAccess world, int x, int y, int z, int metadata)
    {
        int mask = 0;
        int facing = metadata & 7;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
        {
            if (dir != ForgeDirection.getOrientation(facing))
            {
                if (isValidConnectable(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir))
                {
                    mask |= 1 << dir.ordinal();
                }
            }

        }
        return mask;
    }

    @Override
    public boolean acceptsConnectionFrom(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection direction)
    {
        return direction.ordinal() != (metadata & 7);
    }

    @Override
    public int getRenderType()
    {
        return flatNodeRenderID;
    }
}
