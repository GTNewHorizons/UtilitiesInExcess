package com.fouristhenumber.utilitiesinexcess.utils;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class ItemFilter
{
    private final Set<ItemStackKey> whitelist = new HashSet<>();
    private final Set<ItemStackKey> blacklist = new HashSet<>();

    private final List<Predicate<ItemStack>> predicates = new ArrayList<>();

    public boolean matches(ItemStack stack) {
        if (whitelist.contains(new ItemStackKey(stack))) return true;

        for (Predicate<ItemStack> p : predicates) {
            if (p.test(stack)) return true;
        }

        return false;
    }

    public void addToWhiteList(ItemStack stack)
    {
        whitelist.add(new ItemStackKey(stack));
    }

    public void addToPredicates(Predicate<ItemStack> pred)
    {
        predicates.add(pred);
    }
}
