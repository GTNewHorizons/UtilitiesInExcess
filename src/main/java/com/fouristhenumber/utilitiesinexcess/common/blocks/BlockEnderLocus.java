package com.fouristhenumber.utilitiesinexcess.common.blocks;

import static com.fouristhenumber.utilitiesinexcess.utils.UIEUtils.dropItemsFromIInventory;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityEnderLocus;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEnderLocus extends BlockContainer {

    public BlockEnderLocus() {
        super(Material.iron);
        setBlockName("ender_locus");
        setBlockTextureName("utilitiesinexcess:ender_locus");
        setHardness(5.0F);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityEnderLocus();
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        if (!worldIn.isRemote) {
            TileEntityEnderLocus locus = (TileEntityEnderLocus) worldIn.getTileEntity(x, y, z);
            locus.scan();
        }
        super.onBlockAdded(worldIn, x, y, z);
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

    public IIcon sideTexture;
    public IIcon bottomTexture;

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(this.getTextureName() + "_top");
        this.sideTexture = reg.registerIcon(this.getTextureName() + "_side");
        this.bottomTexture = reg.registerIcon(this.getTextureName() + "_bottom");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? this.blockIcon : (side == 0 ? bottomTexture : this.sideTexture);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess worldIn, int x, int y, int z, int side) {
        return getIcon(side, 0);
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (te instanceof IInventory inv) {
            dropItemsFromIInventory(0, inv, worldIn, te.xCoord, te.yCoord, te.zCoord);
        }
        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }
}
