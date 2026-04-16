package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCompressed extends Block {

    String name;
    Block base;

    public BlockCompressed(Block base, String name) {
        super(base.getMaterial());
        // These parameters are unused by the default method
        setHardness(base.getBlockHardness(null, 0, 0, 0) * 1.5F);
        this.name = name;
        this.base = base;
        setBlockName(name);
    }

    public Block getBase() {
        return base;
    }

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public int damageDropped(int meta) {
        return meta;
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < 8; ++i) {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons = new IIcon[16];
        for (int i = 0; i < 8; i++) {
            icons[i] = iconRegister.registerIcon("utilitiesinexcess:" + name + "_" + i);
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

    public static class ItemCompressedBlock extends ItemBlock {

        public ItemCompressedBlock(Block block) {
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

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean bool) {
            tooltip.add(
                StatCollector.translateToLocalFormatted(
                    getUnlocalizedName() + ".desc",
                    (long) Math.pow(9, stack.getItemDamage() + 1)));
        }
    }
}
