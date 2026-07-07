package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLapisAetheriusDyable extends BlockColored {

    public BlockLapisAetheriusDyable() {
        super(ModBlocks.LAPIS_AETHERIUS.get());
        setBlockName("lapis_aetherius_dyeable");
        setLightOpacity(0);
        setHardness(1);
        setResistance(10F);
    }

    @Override
    public boolean ignoreBaseMeta() {
        return true;
    }

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
        list.add(new ItemStack(itemIn, 1, 0b0_11111_11111_11111));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.blockIcon = ((BlockLapisAetherius) ModBlocks.LAPIS_AETHERIUS.get()).icons[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return blockIcon;
    }

    public static class ItemLapisAetherius extends BlockColored.ItemBlockColored {

        public ItemLapisAetherius(Block block) {
            super(block);
        }

        @Override
        public String getItemStackDisplayName(ItemStack p_77653_1_) {
            return StatCollector.translateToLocal("tile.lapis_aetherius_dyeable.name");
        }
    }
}
