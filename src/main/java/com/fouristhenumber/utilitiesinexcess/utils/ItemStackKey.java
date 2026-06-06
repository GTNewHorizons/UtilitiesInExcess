package com.fouristhenumber.utilitiesinexcess.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Objects;

class ItemStackKey {
    private final Item item;
    private final int meta;
    private final NBTTagCompound tag;

    public ItemStackKey(ItemStack stack) {
        this.item = stack.getItem();
        this.meta = stack.getItemDamage();
        this.tag = stack.stackTagCompound == null ? null : (NBTTagCompound) stack.stackTagCompound.copy();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ItemStackKey)) return false;
        ItemStackKey other = (ItemStackKey) o;

        return item == other.item
            && meta == other.meta
            && Objects.equals(tag, other.tag);
    }

    @Override
    public int hashCode() {
        int result = item.hashCode();
        result = 31 * result + meta;
        result = 31 * result + (tag != null ? tag.hashCode() : 0);
        return result;
    }
}
