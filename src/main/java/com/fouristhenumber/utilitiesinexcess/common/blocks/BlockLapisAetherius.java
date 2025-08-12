package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLapisAetherius extends Block {

    public BlockLapisAetherius() {
        super(Material.glass);
        setBlockName("lapis_aetherius");
        setLightOpacity(0);
        setHardness(1);
        setResistance(8F);
    }

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public int getRenderType() {
        return UtilitiesInExcess.lapisAetheriusRenderID;
    }

    @Override
    public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        return 10;
    }

    @Override
    public int getLightValue() {
        return 10;
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < 16; ++i) {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons = new IIcon[16];
        for (int i = 0; i < 16; i++) {
            icons[i] = iconRegister.registerIcon("utilitiesinexcess:lapis_aetherius_" + i);
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

    public static class ItemLapisAetherius extends ItemBlock {

        public ItemLapisAetherius(Block block) {
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
