package com.fouristhenumber.utilitiesinexcess.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityPacifistsBench;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPacifistsBench extends BlockContainer {

    public BlockPacifistsBench() {
        super(Material.wood);
        setBlockTextureName("utilitiesinexcess:pacifists_bench");
        setBlockName("pacifists_bench");
    }

    IIcon[] icons = new IIcon[6];

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(this.getTextureName() + "_west");
        icons[0] = reg.registerIcon(this.getTextureName() + "_bottom");
        icons[1] = reg.registerIcon(this.getTextureName() + "_top");
        icons[2] = reg.registerIcon(this.getTextureName() + "_north");
        icons[3] = reg.registerIcon(this.getTextureName() + "_south");
        icons[4] = blockIcon;
        icons[5] = reg.registerIcon(this.getTextureName() + "_east");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return icons[side];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess worldIn, int x, int y, int z, int side) {
        return getIcon(side, 0);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityPacifistsBench();
    }

    @Override
    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return 10;
    }
}
