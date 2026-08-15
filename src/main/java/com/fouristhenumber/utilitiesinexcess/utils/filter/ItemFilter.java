package com.fouristhenumber.utilitiesinexcess.utils.filter;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class ItemFilter
{
    private final Reference2ObjectOpenHashMap<Item, ItemRules> itemRules = new Reference2ObjectOpenHashMap<>();
    private final PredicateRules predicateRules = new PredicateRules();

    // Blacklists override whitelists. If an item is found in the blacklist it doesn't matter if the item
    // has been whitelisted, but we do need to know if there are whitelists at all so we do need a flag here.
    // Counts for both predicates whitelists and item whitelists.
    boolean hasWhitelist = false;

    // Heavily commented because this logic is fucking confusing for me.
    public boolean matches(ItemStack stack)
    {
        // If there's no rules in the whole filter, just do nothing.
        // Don't think this can happen, but guarding anyway.
        if (itemRules.isEmpty() && predicateRules.isEmpty())
        {
            return true;
        }

        // If we have a rule, check it, otherwise if there's no rule for the item we return if no specific whitelist or predicate whitelist
        // exists on the whole filter. This means we have blacklist only filter, and we can accept the item.
        ItemRules rules = itemRules.get(stack.getItem());
        if (rules == null)
        {
            // so here we couldn't find a rule for this block, now we check if there is actually no rules at all
            if (itemRules.isEmpty())
            {
                // If there are no rules check the predicates of this filter
                if (predicateRules.matchesBlackLists(stack))
                {
                    return false;
                }
                else if (predicateRules.matchesWhiteLists(stack))
                {
                    return true;
                }
            }
            return !hasWhitelist;
        }

        // Check the blacklists
        if (rules.matchesBlacklists(stack) || predicateRules.matchesBlackLists(stack))
        {
            return false;
        }

        // Check the whitelists
        return rules.matchesWhitelists(stack) && predicateRules.matchesWhiteLists(stack);
    }

    public void addToRuleList(ItemStack stack, boolean inverted, MatchMode mode)
    {
        if (stack.getItem() != null)
        {
            ItemRules rules = itemRules.get(stack.getItem());
            if (rules == null)
            {
                rules = new ItemRules();
                itemRules.put(stack.getItem(), rules);
            }
            rules.addRule(stack, inverted, mode);

            if (!inverted)
            {
                hasWhitelist = true;
            }
        }
    }

    public void addToPredicates(Predicate<ItemStack> pred, boolean inverted)
    {
        predicateRules.add(pred, inverted);
        if (!inverted)
        {
            hasWhitelist = true;
        }
    }
}
