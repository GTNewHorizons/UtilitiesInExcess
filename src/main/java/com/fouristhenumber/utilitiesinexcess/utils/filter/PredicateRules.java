package com.fouristhenumber.utilitiesinexcess.utils.filter;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PredicateRules
{
    private final List<Predicate<ItemStack>> whiteListPredicates = new ArrayList<>();
    private final List<Predicate<ItemStack>> blackListPredicates = new ArrayList<>();

    public boolean isEmpty()
    {
        return whiteListPredicates.isEmpty() && blackListPredicates.isEmpty();
    }

    // All whitelists must match to pass
    public boolean matchesWhiteLists(ItemStack stack)
    {
        for (Predicate<ItemStack> whiteListPredicate : whiteListPredicates)
        {
            if (!whiteListPredicate.test(stack))
            {
                return false;
            }
        }
        return true;
    }

    // Only one blacklist must match to fail
    public boolean matchesBlackLists(ItemStack stack)
    {
        for (Predicate<ItemStack> blackListPredicate : blackListPredicates)
        {
            if (blackListPredicate.test(stack))
            {
                return true;
            }
        }
        return false;
    }

    public void add(Predicate<ItemStack> pred, boolean inverted)
    {
        if (inverted)
        {
            blackListPredicates.add(pred);
        }
        else
        {
            whiteListPredicates.add(pred);
        }
    }
}
