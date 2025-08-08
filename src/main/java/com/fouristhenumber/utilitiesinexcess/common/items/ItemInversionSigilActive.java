package com.fouristhenumber.utilitiesinexcess.common.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemInversionSigilActive extends Item {
    public ItemInversionSigilActive() {
        super();
        setUnlocalizedName("inversion_sigil_active");
        setTextureName("utilitiesinexcess:inversion_sigil_active");
        setMaxStackSize(1);
        setContainerItem(this);
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack par1ItemStack) {
        return false;
    }
}
