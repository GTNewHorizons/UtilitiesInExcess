package com.fouristhenumber.utilitiesinexcess.common.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemPseudoDivisionSigil extends Item {

    public ItemPseudoDivisionSigil() {
        super();
        setUnlocalizedName("pseudo_division_sigil");
        setTextureName("utilitiesinexcess:pseudo_division_sigil");
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
