package com.fouristhenumber.utilitiesinexcess.common.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemPseudoReversionSigil extends Item {

    public ItemPseudoReversionSigil() {
        super();
        setUnlocalizedName("pseudo_reversion_sigil");
        setTextureName("utilitiesinexcess:pseudo_reversion_sigil");
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
