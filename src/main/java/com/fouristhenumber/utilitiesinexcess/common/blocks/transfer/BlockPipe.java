package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import com.cleanroommc.modularui.factory.GuiFactories;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityFilterPipe;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer.PipePart;
import com.fouristhenumber.utilitiesinexcess.compat.Mods;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.collision.PipeCollision;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.BaseInserter;
import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

public class BlockPipe extends BlockTransferBase
{

    public BlockPipe() {
        super(Material.iron);
        this.setBlockName("block_pipe");
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
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg)
    {
        for (int i = 0; i < PipeType.values().length; i++)
        {
            PipeType.values()[i].registerIcon(reg);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta)
    {
        return PipeType.fromMeta(meta).getIcon(side);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        if (world.getBlock(x, y, z) instanceof BlockPipe)
        {
            return getIcon(side, world.getBlockMetadata(x, y, z));
        }

        // Weird to put this here, but the normal render blocks pipeline goes through here.
        // There are probably better ways to do this, but it takes a lot of changes to rendering
        // from what I can tell.
        if (Mods.ForgeMicroBlock.isLoaded())
        {
            if (world.getTileEntity(x, y, z) instanceof TileMultipart multipart)
            {
                for (TMultiPart part : multipart.jPartList())
                {
                    if (part instanceof PipePart pipe)
                    {
                        return getIcon(side, pipe.meta);
                    }
                }
            }
        }
        return getIcon(0, 0);
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<net.minecraft.item.ItemStack> list)
    {
        for (int i = 0; i < PipeType.values().length; i++)
        {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }

    @Override
    public MovingObjectPosition collisionRayTrace(
        World world,
        int x,
        int y,
        int z,
        Vec3 startVec,
        Vec3 endVec)
    {
        List<IConnectable> connectables = IConnectable.getConnectables(
            world,
            x,
            y,
            z
        );

        int mask = IConnectable.getConnectionMask(
            connectables,
            world,
            x,
            y,
            z
        );

        MovingObjectPosition closestHit = null;
        double closestDistance = Double.MAX_VALUE;

        for (PipeCollision collision : PipeCollision.values())
        {
            // MIDDLE is always present.
            if (collision != PipeCollision.MIDDLE
                && (mask & (1 << collision.getMaskBit())) == 0)
            {
                continue;
            }

            AxisAlignedBB box = collision.getCollisionBox().copy();

            // collision boxes are block-local, while the ray is in world space
            box = box.getOffsetBoundingBox(x, y, z);

            MovingObjectPosition hit = box.calculateIntercept(
                startVec,
                endVec
            );

            if (hit == null)
                continue;

            double distance = hit.hitVec.squareDistanceTo(startVec);

            if (distance < closestDistance)
            {
                closestDistance = distance;

                closestHit = new MovingObjectPosition(
                    x,
                    y,
                    z,
                    hit.sideHit,
                    hit.hitVec
                );
            }
        }

        return closestHit;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
        List<IConnectable> connectables = IConnectable.getConnectables(world, x, y, z);
        if (!connectables.isEmpty())
        {
            int mask = IConnectable.getConnectionMask(connectables, world, x, y, z);

            float minY = (mask & (1 << 0)) != 0 ? 0F : 0.375F; // DOWN (-Y)
            float maxY = (mask & (1 << 1)) != 0 ? 1F : 0.625F; // UP (+Y)

            float minZ = (mask & (1 << 2)) != 0 ? 0F : 0.375F; // NORTH (-Z)
            float maxZ = (mask & (1 << 3)) != 0 ? 1F : 0.625F; // SOUTH (+Z)

            float minX = (mask & (1 << 4)) != 0 ? 0F : 0.375F; // WEST (-X)
            float maxX = (mask & (1 << 5)) != 0 ? 1F : 0.625F; // EAST (+X)

            this.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
        }
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collider)
    {
        for (AxisAlignedBB box : getBlockCenteredCollisionCandidates(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z)))
        {
            if (box.offset(x, y, z).intersectsWith(mask))
            {
                list.add(box);
            }
        }
    }


    public static List<AxisAlignedBB> getBlockCenteredCollisionCandidates(World worldIn, int x, int y, int z, int meta)
    {
        List<AxisAlignedBB> candidates = new ArrayList<>();
        List<IConnectable> connectables = IConnectable.getConnectables(worldIn, x, y, z);
        if (!connectables.isEmpty())
        {
            int connectionMask = IConnectable.getConnectionMask(connectables, worldIn, x, y, z);

            candidates.add(PipeCollision.MIDDLE.getCollisionBox().copy());


            for (int i = 1; i < PipeCollision.values().length; i++)
            {
                if ((connectionMask & (1 << (i - 1))) != 0)
                {
                    candidates.add(PipeCollision.values()[i].getCollisionBox().copy());
                }
            }
        }
        return candidates;
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX,
                                    float subY, float subZ)
    {
        if (!worldIn.isRemote)
        {
            if (worldIn.getBlockMetadata(x, y, z) == PipeType.FILTER.ordinal() && worldIn.getTileEntity(x, y, z) instanceof TileEntityFilterPipe)
            {
                GuiFactories.tileEntity().open(player, x, y, z);
                return true;
            }
        }
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityFilterPipe();
    }

    @Override
    public boolean hasTileEntity(int metadata)
    {
        return metadata == PipeType.FILTER.getMeta();
    }

    @Override
    public int getRenderType()
    {
        return ModelISBRH.JSON_ISBRH_ID;
    }

    @Override
    public int validWalkDirections(IBlockAccess world, int x, int y, int z, ForgeDirection fromDirection, IWalkingComponent<?> walkingComponent)
    {
        return PipeType.values()[world.getBlockMetadata(x, y, z)].validWalkDirections(world, x, y, z, fromDirection, walkingComponent);
    }

    @Override
    public int getConnectionMask(IBlockAccess world, int x, int y, int z)
    {
        return PipeType.values()[world.getBlockMetadata(x, y, z)].getConnectionMask(world, x, y, z);
    }

    @Override
    public boolean getConnection(IBlockAccess world, int x, int y, int z, ForgeDirection dir) {
        return PipeType.values()[world.getBlockMetadata(x, y, z)].getConnection(world, x, y, z, dir);
    }

    @Override
    public boolean canConnectInDirection(IBlockAccess world, int x, int y, int z, ForgeDirection direction)
    {
        return PipeType.values()[world.getBlockMetadata(x, y, z)].acceptsConnectionFrom(world, x, y, z, direction);
    }

    @Override
    public BaseInserter getInserter(IBlockAccess world, int x, int y, int z) {
        return PipeType.values()[world.getBlockMetadata(x, y, z)].getInserter();
    }

}
