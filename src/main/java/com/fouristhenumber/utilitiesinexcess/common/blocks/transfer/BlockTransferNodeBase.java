package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import static com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess.transferNodeRenderID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityTransferNode;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockTransferNodeBase extends BlockContainer {

    protected BlockTransferNodeBase() {
        super(Material.iron);
        setBlockTextureName(getTopIcon());
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
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

    protected void updateConnections(World world, int x, int y, int z) {
        if (!world.isRemote) {
            TileEntityTransferNode te = (TileEntityTransferNode) world.getTileEntity(x, y, z);
            if (te != null) {
                boolean changed = te.updateConnections(world, x, y, z);
                if (changed) {
                    world.markBlockForUpdate(x, y, z);
                }
            }
        }
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
        return ForgeDirection.getOrientation(side)
            .getOpposite()
            .ordinal();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, placer, stack);
        int meta = world.getBlockMetadata(x, y, z);
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
        updateConnections(world, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        updateConnections(world, x, y, z);
    }

    @Override
    public int getRenderType() {
        return transferNodeRenderID;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (side == meta) return icons[0];
        return icons[1];
    }

    @SideOnly(Side.CLIENT)
    protected IIcon[] icons;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        icons = new IIcon[2];
        icons[0] = reg.registerIcon("utilitiesinexcess:transfer_node_bottom");
        icons[1] = reg.registerIcon(getTopIcon());
    }

    public String getTopIcon() {
        return "utilitiesinexcess:transfer_node_top";
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        switch (meta) {
            case 0:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
                break;
            case 1:
                this.setBlockBounds(0.0F, 1.0F - 0.625F, 0.0F, 1.0F, 1.0F, 1.0F);
                break;
            case 2:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.625F);
                break;
            case 3:
                this.setBlockBounds(0.0F, 0.0F, 1.0F - 0.625F, 1.0F, 1.0F, 1.0F);
                break;
            case 4:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.625F, 1.0F, 1.0F);
                break;
            case 5:
                this.setBlockBounds(1.0F - 0.625F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                break;
        }
    }
}
