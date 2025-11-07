package com.fouristhenumber.utilitiesinexcess.transfer.upgrade;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.ModItems;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.BreadthWalker;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.DepthWalker;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.ITransferWalker;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.RoundRobinWalker;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public enum TransferUpgrade {

    SPEED(ModItems.SPEED_UPGRADE.get(), null),
    ITEM_FILTER(ModItems.ITEM_FILTER.get(), UpgradeType.FILTER),
    ADV_ITEM_FILTER(ModItems.ADV_ITEM_FILTER.get(), UpgradeType.FILTER),
    WORLD_INTERACTION(ModItems.WORLD_INTERACTION_UPGRADE.get(), null),
    STACK(ModItems.STACK_UPGRADE.get(), null),
    ENDER_TRANSMITTER(ModItems.ENDER_TRANSMITTER.get(), null),
    ENDER_RECEIVER(ModItems.ENDER_RECEIVER.get(), null),
    SEARCH_DEPTH(ModItems.SEARCH_DEPTH_UPGRADE.get(), UpgradeType.WALKER),
    SEARCH_BREADTH(ModItems.SEARCH_BREADTH_UPGRADE.get(), UpgradeType.WALKER),
    SEARCH_ROUND_ROBIN(ModItems.SEARCH_ROUND_ROBIN_UPGRADE.get(), UpgradeType.WALKER),
    CREATIVE(ModItems.CREATIVE_UPGRADE.get(), null),

    ;

    private static final BiMap<TransferUpgrade, Item> MAPPING = HashBiMap.create(values().length);
    static {
        for (TransferUpgrade upgrade : values()) {
            MAPPING.put(upgrade, upgrade.item);
        }
    }

    private final Item item;
    private final UpgradeType type;

    TransferUpgrade(Item item, UpgradeType type) {
        this.item = item;
        this.type = type;
    }

    public boolean isWalkerUpgrade() {
        return this.type == UpgradeType.WALKER;
    }

    public ITransferWalker getWalker() {
        return switch (this) {
            case SEARCH_DEPTH -> new DepthWalker();
            case SEARCH_BREADTH -> new BreadthWalker();
            case SEARCH_ROUND_ROBIN -> new RoundRobinWalker();
            default -> null;
        };
    }

    public boolean isFilterUpgrade() {
        return this.type == UpgradeType.FILTER;
    }

    public static boolean isUpgrade(ItemStack stack) {
        return getUpgrade(stack) != null;
    }

    public static TransferUpgrade getUpgrade(ItemStack stack) {
        if (stack == null) return null;
        return MAPPING.inverse()
            .get(stack.getItem());
    }

    private enum UpgradeType {
        WALKER,
        FILTER,
    }
}
