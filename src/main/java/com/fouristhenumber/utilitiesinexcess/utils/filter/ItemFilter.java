package com.fouristhenumber.utilitiesinexcess.utils.filter;

import java.util.function.Predicate;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

public class ItemFilter {

    private final Reference2ObjectOpenHashMap<Item, ItemRules> itemRules = new Reference2ObjectOpenHashMap<>();
    private final PredicateRules predicateRules = new PredicateRules();

    // Blacklists override whitelists. If an item is found in the blacklist it doesn't matter if the item
    // has been whitelisted, but we do need to know if there are whitelists at all so we do need a flag here.
    // Counts for both predicates whitelists and item whitelists.
    boolean hasWhitelist = false;

    // Heavily commented because this logic is fucking confusing for me.
    public boolean matches(ItemStack stack) {
        if (itemRules.isEmpty() && predicateRules.isEmpty()) {
            return true;
        }

        // Blacklists always take precedence.
        ItemRules rules = itemRules.get(stack.getItem());

        if ((rules != null && rules.matchesBlacklists(stack)) || predicateRules.matchesBlackLists(stack)) {
            return false;
        }

        // An item without specific rules can still match predicates.
        if (rules == null) {
            if (predicateRules.matchesWhiteLists(stack)) {
                return true;
            }

            return !hasWhitelist;
        }

        // Both rule systems must permit the item.
        return rules.matchesWhitelists(stack) && predicateRules.matchesWhiteLists(stack);
    }

    public void addToRuleList(ItemStack stack, boolean inverted, MatchMode mode) {
        if (stack.getItem() != null) {
            ItemRules rules = itemRules.get(stack.getItem());
            if (rules == null) {
                rules = new ItemRules();
                itemRules.put(stack.getItem(), rules);
            }
            rules.addRule(stack, inverted, mode);

            if (!inverted) {
                hasWhitelist = true;
            }
        }
    }

    public void addToPredicates(Predicate<ItemStack> pred, boolean inverted) {
        predicateRules.add(pred, inverted);
        if (!inverted) {
            hasWhitelist = true;
        }
    }
}
