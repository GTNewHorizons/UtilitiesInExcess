package com.fouristhenumber.utilitiesinexcess.transfer.upgrade;

import net.minecraft.item.ItemStack;

public interface IUpgradeable {
    default void applySpeedUpgrade(ItemStack stack) {}
    default void applyFilterUpgrade(ItemStack stack) {}
    default void applyAdvFilterUpgrade(ItemStack stack) {}
    default void applyWorldInteractionUpgrade(ItemStack stack) {}
    default void applyStackUpgrade(ItemStack stack) {}
    default void applyCreativeUpgrade(ItemStack stack) {}
    default void applyEnderTransmitterUpgrade(ItemStack stack) {}
    default void applyEnderReceiverUpgrade(ItemStack stack) {}
    default void applySearchDepthUpgrade(ItemStack stack) {}
    default void applySearchBreadthUpgrade(ItemStack stack) {}
    default void applySearchRoundRobinUpgrade(ItemStack stack) {}

    void resetUpgrades();
    void markDirty();
}

