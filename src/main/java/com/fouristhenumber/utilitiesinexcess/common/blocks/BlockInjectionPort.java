package com.fouristhenumber.utilitiesinexcess.common.blocks;

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

import com.cleanroommc.modularui.factory.GuiFactories;
import com.fouristhenumber.utilitiesinexcess.common.renderers.InjectionPortRenderer;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityInjectionPort;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockInjectionPort extends BlockContainer {

    public BlockInjectionPort() {
        super(Material.iron);
        setBlockName("injection_port");
        setBlockTextureName("utilitiesinexcess:floating_block");
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityInjectionPort();
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        if (!worldIn.isRemote) GuiFactories.tileEntity()
            .open(player, x, y, z);
        return true;
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
        return side;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        int meta = world.getBlockMetadata(x, y, z);
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return InjectionPortRenderer.RENDER_ID;
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
        icons[0] = reg.registerIcon("utilitiesinexcess:floating_block");
        icons[1] = reg.registerIcon("utilitiesinexcess:cursed_earth");
    }

    private static final float THICKNESS = 2f / 16f;

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        switch (meta) {
            case 0: // Down
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, THICKNESS, 1.0F);
                break;
            case 1: // Up
                this.setBlockBounds(0.0F, 1.0F - THICKNESS, 0.0F, 1.0F, 1.0F, 1.0F);
                break;
            case 2: // North
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, THICKNESS);
                break;
            case 3: // South
                this.setBlockBounds(0.0F, 0.0F, 1.0F - THICKNESS, 1.0F, 1.0F, 1.0F);
                break;
            case 4: // West
                this.setBlockBounds(0.0F, 0.0F, 0.0F, THICKNESS, 1.0F, 1.0F);
                break;
            case 5: // East
                this.setBlockBounds(1.0F - THICKNESS, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                break;
        }
    }
}
