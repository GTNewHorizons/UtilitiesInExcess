package com.fouristhenumber.utilitiesinexcess.utils.filter;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemRules
{

    private final List<ItemRule> blacklists = new ArrayList<>();
    private final List<ItemRule> whitelists = new ArrayList<>();

    public boolean matchesBlacklists(ItemStack stack) {

        for (ItemRule rule : blacklists) {
            if (rule.matches(stack)) {
                return true;
            }
        }
        return false;
    }

    public boolean matchesWhitelists(ItemStack stack)
    {
        if (whitelists.isEmpty()) {
            return true;
        }

        for (ItemRule rule : whitelists) {
            if (rule.matches(stack)) {
                return true;
            }
        }

        return false;
    }

    public void addRule(ItemStack stack, boolean inverted, MatchMode mode)
    {
        if (inverted)
        {
            blacklists.add(new ItemRule(mode, stack.getItemDamage(), stack.stackTagCompound));
        }
        else
        {
            whitelists.add(new ItemRule(mode, stack.getItemDamage(), stack.stackTagCompound));
        }
    }
}
