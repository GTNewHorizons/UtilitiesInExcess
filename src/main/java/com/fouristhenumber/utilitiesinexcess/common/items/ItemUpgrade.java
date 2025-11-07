package com.fouristhenumber.utilitiesinexcess.common.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.transfer.upgrade.TransferUpgrade;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemUpgrade extends Item {

    private static final IIcon[] ICONS = new IIcon[TransferUpgrade.VALUES.length];

    public ItemUpgrade() {
        setUnlocalizedName("upgrade");
        setHasSubtypes(true);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int meta) {
        return ICONS[meta];
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "_" + TransferUpgrade.VALUES[stack.getItemDamage()].getName();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister register) {
        for (TransferUpgrade upgrade : TransferUpgrade.VALUES) {
            ICONS[upgrade.ordinal()] = register.registerIcon(UtilitiesInExcess.MODID + ":" + upgrade.getName());
        }
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> items) {
        for (TransferUpgrade upgrade : TransferUpgrade.VALUES) {
            items.add(new ItemStack(item, 1, upgrade.ordinal()));
        }
    }
}
