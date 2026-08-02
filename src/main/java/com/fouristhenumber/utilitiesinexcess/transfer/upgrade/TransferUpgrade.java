package com.fouristhenumber.utilitiesinexcess.transfer.upgrade;

import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemUpgrade;
import com.fouristhenumber.utilitiesinexcess.common.recipe.DisableableItemStack;

public enum TransferUpgrade {

    // Order preserved from XU for migration purposes
    SPEED,
    FILTER,
    WORLD_INTERACTION,
    STACK,
    CREATIVE,
    ENDER_TRANSMITTER,
    ENDER_RECEIVER,
    SEARCH_DEPTH,
    SEARCH_BREADTH,
    SEARCH_ROUND_ROBIN,
    ADV_FILTER,
    ;

    public static final TransferUpgrade[] VALUES = values();

    public String getName() {
        return name().toLowerCase();
    }

    public DisableableItemStack getStack() {
        return getStack(1);
    }

    public DisableableItemStack getStack(int amount) {
        return new DisableableItemStack(ModItems.UPGRADE, amount, ordinal());
    }

    public static boolean isUpgrade(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemUpgrade;
    }

    public static TransferUpgrade getUpgrade(ItemStack stack) {
        if (!isUpgrade(stack)) return null;

        int meta = stack.getItemDamage();
        if (meta < 0 || meta >= VALUES.length) return null;
        return VALUES[meta];
    }

    public void applyTo(IUpgradeable node, ItemStack stack) {
        switch (this) {
            case SPEED:                node.applySpeedUpgrade(stack); break;
            case FILTER:                node.applyFilterUpgrade(stack); break;
            case WORLD_INTERACTION:      node.applyWorldInteractionUpgrade(stack); break;
            case STACK:                  node.applyStackUpgrade(stack); break;
            case CREATIVE:                node.applyCreativeUpgrade(stack); break;
            case ENDER_TRANSMITTER:      node.applyEnderTransmitterUpgrade(stack); break;
            case ENDER_RECEIVER:          node.applyEnderReceiverUpgrade(stack); break;
            case SEARCH_DEPTH:            node.applySearchDepthUpgrade(stack); break;
            case SEARCH_BREADTH:          node.applySearchBreadthUpgrade(stack); break;
            case SEARCH_ROUND_ROBIN:      node.applySearchRoundRobinUpgrade(stack); break;
            case ADV_FILTER:              node.applyAdvFilterUpgrade(stack); break;
        }
    }
}
