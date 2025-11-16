package com.fouristhenumber.utilitiesinexcess.common.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemPseudoInversionSigil extends Item {

    public ItemPseudoInversionSigil() {
        super();
        setUnlocalizedName("pseudo_inversion_sigil");
        setTextureName("utilitiesinexcess:pseudo_inversion_sigil");
        setMaxStackSize(1);
        setContainerItem(this);
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack par1ItemStack) {
        return false;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return itemStack;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tt, boolean p_77624_4_) {
        NBTTagCompound tag = stack.getTagCompound();
        super.addInformation(stack, player, tt, p_77624_4_);
    }
}
