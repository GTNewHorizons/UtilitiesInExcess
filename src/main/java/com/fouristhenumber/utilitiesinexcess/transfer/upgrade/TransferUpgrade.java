package com.fouristhenumber.utilitiesinexcess.transfer.upgrade;

import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.common.items.ItemUpgrade;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.BreadthWalker;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.DepthWalker;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.ITransferWalker;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.RoundRobinWalker;

public enum TransferUpgrade {

    // Order preserved from XU for migration purposes
    SPEED(null),
    FILTER(UpgradeType.FILTER),
    WORLD_INTERACTION(null),
    STACK(null),
    CREATIVE(null),
    ENDER_TRANSMITTER(null),
    ENDER_RECEIVER(null),
    SEARCH_DEPTH(UpgradeType.WALKER),
    SEARCH_BREADTH(UpgradeType.WALKER),
    SEARCH_ROUND_ROBIN(UpgradeType.WALKER),
    ADV_FILTER(UpgradeType.FILTER),

    ;

    public static final TransferUpgrade[] VALUES = values();

    private final UpgradeType type;

    TransferUpgrade(UpgradeType type) {
        this.type = type;
    }

    public String getName() {
        return name().toLowerCase();
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
        return stack != null && stack.getItem() instanceof ItemUpgrade;
    }

    public static TransferUpgrade getUpgrade(ItemStack stack) {
        if (!isUpgrade(stack)) return null;

        int meta = stack.getItemDamage();
        if (meta < 0 || meta >= VALUES.length) return null;
        return VALUES[meta];
    }

    private enum UpgradeType {
        WALKER,
        FILTER,
    }
}
