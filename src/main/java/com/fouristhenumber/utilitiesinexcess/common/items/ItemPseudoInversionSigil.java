package com.fouristhenumber.utilitiesinexcess.common.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
}
