package com.fouristhenumber.utilitiesinexcess.api;

import java.util.Objects;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Wrapper class of ItemStack that compares with itemStack Item, Count and Damage.
 */

public class ItemStackBaseCompare {

    public ItemStackBaseCompare(ItemStack itemStack) {
        itemStackItem = itemStack.getItem();
        itemStackCount = itemStack.stackSize;
        itemStackDamage = itemStack.getItemDamage();
    }

    Item itemStackItem;
    int itemStackCount;
    int itemStackDamage;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ItemStackBaseCompare that)) return false;
        return itemStackCount == that.itemStackCount && itemStackDamage == that.itemStackDamage
            && Objects.equals(itemStackItem, that.itemStackItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemStackItem, itemStackCount, itemStackDamage);
    }
}
