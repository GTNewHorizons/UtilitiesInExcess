package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.pipe;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockTransferBase;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityNetworkComponentBase;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe.TileEntityCrossoverPipe;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe.TileEntityEnergyExtractionPipe;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe.TileEntityEnergyPipe;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe.TileEntityFilterPipe;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe.TileEntityHyperRationingPipe;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe.TileEntityModSortingPipe;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe.TileEntityRationingPipe;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe.TileEntitySortingPipe;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe.TileEntityTransferPipe;
import com.fouristhenumber.utilitiesinexcess.transfer.collision.PipeCollision;
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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

import static com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess.transferPipeRenderID;

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
    public boolean hasTileEntity(int metadata)
    {
        return true;
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

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return PipeType.fromMeta(meta).createTileEntity();
    }


    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta)
    {
        return PipeType.fromMeta(meta).getIcon(side);
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
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        int meta = world.getBlockMetadata(x, y, z);
        return PipeType.fromMeta(meta).getIcon(side);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
        TileEntityNetworkComponentBase<?> te = (TileEntityNetworkComponentBase<?>) world.getTileEntity(x, y, z);
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

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX,
                                    float subY, float subZ)
    {

        if (!worldIn.isRemote)
        {
            if (worldIn.getBlockMetadata(x, y, z) == 2 && worldIn.getTileEntity(x, y, z) instanceof TileEntityFilterPipe)
            {
                GuiFactories.tileEntity().open(player, x, y, z);
            }
        }
        return true;
    }

    @Override
    public int getRenderType()
    {
        return transferPipeRenderID;
    }
}
