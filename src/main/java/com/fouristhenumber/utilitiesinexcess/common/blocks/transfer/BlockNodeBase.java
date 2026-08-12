package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;


import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.collision.NodeCollision;
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

import java.util.Arrays;
import java.util.List;

import static com.fouristhenumber.utilitiesinexcess.CommonProxy.flatNodeRenderID;
import static com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.NetworkLogic.isValidConnectable;

public abstract class BlockNodeBase extends BlockTransferBase
{
    private static final int TYPE_MASK = 0b00000001;
    private static final int FACING_MASK = 0b00001110;
    private static final int FACING_SHIFT = 1;


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
        return  meta | (ForgeDirection.getOrientation(side).getOpposite().ordinal() << FACING_SHIFT);
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collider)
    {
        Block block = worldIn.getBlock(x, y, z);
        int facing = getFacingOrdinal(worldIn.getBlockMetadata(x, y, z));
        if (block instanceof BlockTransferBase transferBase)
        {
            int connectionMask = transferBase.getConnectionMask(worldIn, x, y, z);

            AxisAlignedBB boundingBox = PipeCollision.MIDDLE.getCollisionBox().copy().offset(x, y, z);
            if (connectionMask != 0 && boundingBox.intersectsWith(mask))
            {
                list.add(boundingBox);
            }

            for (int i = 1; i < PipeCollision.values().length; i++)
            {
                boundingBox = PipeCollision.values()[i].getCollisionBox().copy().offset(x, y, z);
                if ((connectionMask & (1 << (i - 1))) != 0 && boundingBox.intersectsWith(mask))
                {
                    list.add(boundingBox);
                }
            }
            list.addAll(
                Arrays.stream(NodeCollision.values()[facing].getCollisionBoxes())
                    .map(b -> b.copy().offset(x, y, z))
                    .filter(b -> b.intersectsWith(mask))
                    .toList()
            );
        }
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
        Block block = world.getBlock(x, y, z);

        if (!(block instanceof BlockNodeBase nodeBase))
            return;

        int meta = world.getBlockMetadata(x, y, z);
        int mask = nodeBase.getConnectionMask(world, x, y, z);
        AxisAlignedBB bb = getBoundsAABB(meta, mask);
        block.setBlockBounds(
            (float) bb.minX,
            (float) bb.minY,
            (float) bb.minZ,
            (float) bb.maxX,
            (float) bb.maxY,
            (float) bb.maxZ
        );
    }

    public static AxisAlignedBB getBoundsAABB(int meta, int connectionMask)
    {
        int facing = getFacingOrdinal(meta);

        AxisAlignedBB bb = NodeCollision.values()[facing].getBoundingBox().copy();
        if (connectionMask != 0) {

            // Need to add in the middle bounding box always if it's
            switch(facing)
            {
                case(0):
                {
                    bb.maxY = PipeCollision.MIDDLE.getCollisionBox().maxY;
                    break;
                }
                case(1):
                {
                    bb.minY = PipeCollision.MIDDLE.getCollisionBox().minY;
                    break;
                }
                case(2):
                {
                    bb.maxZ = PipeCollision.MIDDLE.getCollisionBox().maxZ;
                    break;
                }
                case(3):
                {
                    bb.minZ = PipeCollision.MIDDLE.getCollisionBox().minZ;
                    break;
                }
                case(4):
                {
                    bb.maxX = PipeCollision.MIDDLE.getCollisionBox().maxX;
                    break;
                }
                case(5):
                {
                    bb.minX = PipeCollision.MIDDLE.getCollisionBox().minX;
                    break;
                }
            }

            for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
                if ((connectionMask & (1 << direction.ordinal())) == 0)
                    continue;

                switch (direction) {
                    case DOWN:
                        bb.minY = 0.0;
                        break;

                    case UP:
                        bb.maxY = 1.0;
                        break;

                    case NORTH:
                        bb.minZ = 0.0;
                        break;

                    case SOUTH:
                        bb.maxZ = 1.0;
                        break;

                    case WEST:
                        bb.minX = 0.0;
                        break;

                    case EAST:
                        bb.maxX = 1.0;
                        break;
                }
            }
        }
        return bb;
    }

    @Override
    public boolean hasTileEntity(int metadata)
    {
        return true;
    }

    @Override
    public int validWalkDirections(World world, int x, int y, int z, ForgeDirection fromDirection, IWalkingComponent<?> walkingComponent)
    {
        int mask = 0b111111;
        int facing = getFacingOrdinal(world.getBlockMetadata(x, y, z));
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
    public int getConnectionMask(IBlockAccess world, int x, int y, int z)
    {
        int mask = 0;
        ForgeDirection facing = getFacing(world.getBlockMetadata(x, y, z));
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
        {
            if (dir != facing)
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
    public boolean canConnectInDirection(IBlockAccess world, int x, int y, int z, ForgeDirection direction)
    {
        return direction != getFacing(world.getBlockMetadata(x, y, z));
    }

    @Override
    public int getRenderType()
    {
        return flatNodeRenderID;
    }

    public static ForgeDirection getFacing(int meta)
    {
        return ForgeDirection.getOrientation(getFacingOrdinal(meta));
    }

    public static int getFacingOrdinal(int meta)
    {
        return (meta & FACING_MASK) >> FACING_SHIFT;
    }
}
