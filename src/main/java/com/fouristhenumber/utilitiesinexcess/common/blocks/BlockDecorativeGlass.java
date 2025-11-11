package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDecorativeGlass extends BlockGlass {

    public BlockDecorativeGlass() {
        super(Material.glass, false);
        setBlockName("decorative_glass");
        setHardness(0.3F);
        setStepSound(soundTypeGlass);
    }

    // Dark Glass has the blackout curtain effect
    @Override
    public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z) == 10 ? 8 : super.getLightOpacity(world, x, y, z);
    }

    // Glowing Glass
    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z) == 7 ? 10 : super.getLightValue();
    }

    @Override
    public int getLightValue() {
        return super.getLightValue();
    }

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < 11; ++i) {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons = new IIcon[12];
        for (int i = 0; i < 11; i++) {
            icons[i] = iconRegister.registerIcon("utilitiesinexcess:glass_" + i);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (meta < 0 || meta >= icons.length) {
            meta = 0;
        }
        return icons[meta];
    }

    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass() {
        return 0;
    }

    public static class ItemBlockDecorativeGlass extends ItemBlock {

        public ItemBlockDecorativeGlass(Block block) {
            super(block);
            this.setHasSubtypes(true);
            this.setMaxDamage(0);
        }

        @Override
        public int getMetadata(int damage) {
            return damage;
        }

        @Override
        public String getUnlocalizedName(final ItemStack stack) {
            return this.getUnlocalizedName() + "." + stack.getItemDamage();
        }
    }
}
