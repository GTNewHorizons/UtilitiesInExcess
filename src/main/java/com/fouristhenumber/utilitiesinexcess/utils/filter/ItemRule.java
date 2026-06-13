package com.fouristhenumber.utilitiesinexcess.utils.filter;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemRule
{
    private MatchMode mode;
    private int meta;
    private NBTTagCompound nbt;

    public ItemRule(MatchMode mode, int meta, NBTTagCompound nbt)
    {
        this.mode = mode;
        this.meta = meta;
        this.nbt = nbt;
    }

    // This matches function is called after we've already matched on the item so no need to compare it.
    boolean matches(ItemStack stack)
    {
        return switch (mode) {
            case DEFAULT -> stack.stackTagCompound == nbt && stack.getItemDamage() == meta;
            case IGNORE_NBT -> stack.getItemDamage() == meta;
            case IGNORE_META -> stack.stackTagCompound == nbt;
            case IGNORE_NBT_MET -> true;
        };
    }
}
