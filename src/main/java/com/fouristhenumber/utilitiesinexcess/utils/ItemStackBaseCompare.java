package com.fouristhenumber.utilitiesinexcess.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Wrapper class of ItemStack that compares with itemStack Item, Count and Damage.
 */

public class ItemStackBaseCompare {

    public ItemStackBaseCompare(ItemStack itemStack) {
        item = itemStack.getItem();
        count = itemStack.stackSize;
        damage = itemStack.getItemDamage();
    }

    Item item;
    int count;
    int damage;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ItemStackBaseCompare that)) return false;
        return count == that.count && damage == that.damage && item.equals(that.item);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (item != null ? item.hashCode() : 0);
        result = 31 * result + count;
        result = 31 * result + damage;
        return result;
    }
}
