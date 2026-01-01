package com.fouristhenumber.utilitiesinexcess.inventory;

import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.utils.item.ItemHandlerHelper;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.cabinet.base.TileFilingCabinetBaseItem;

public class CabinetInventory extends ItemStackHandler {

    private final TileFilingCabinetBaseItem cabinet;
    private final int baseCapacity;
    private final int capacityPerUpgrade;
    private int usedSlotCount = 0;
    private int itemCount = 0;

    @Nullable
    private Predicate<ItemStack> itemMatcher = null;

    public CabinetInventory(TileFilingCabinetBaseItem cabinet, int numSlots, int baseCapacity, int capacityPerUpgrade) {
        super(numSlots);
        this.cabinet = cabinet;
        this.baseCapacity = baseCapacity;
        this.capacityPerUpgrade = capacityPerUpgrade;
    }

    @Override
    public int getStackLimit(int slot, @Nullable ItemStack stack) {
        return this.getSlotLimit(slot);
    }

    @Override
    public int getSlotLimit(int slot) {
        ItemStack stack = getStackInSlot(slot);
        return (stack != null) ? stack.stackSize + getRemainingCapacity() : getRemainingCapacity();
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        validateSlotIndex(slot);

        if (stack == null || stack.stackSize <= 0) {
            super.setStackInSlot(slot, null);
            onContentsChanged(slot);
            return;
        }

        // Merge with any compatible slot first
        for (int i = 0; i < getSlots(); i++) {
            if (i == slot) continue;
            ItemStack existing = getStackInSlot(i);
            if (existing != null && ItemHandlerHelper.canItemStacksStack(existing, stack)) {
                long transferable = Math.min(stack.stackSize, getSlotLimit(i));
                transferable = Math.min(transferable, getRemainingCapacity());
                if (transferable > 0) {
                    existing.stackSize += transferable;
                    stack.stackSize -= transferable;
                    onContentsChanged(i);
                }
                if (stack.stackSize <= 0) return;
            }
        }

        // Place leftover in target slot
        super.setStackInSlot(slot, stack.stackSize > 0 ? stack : null);
        onContentsChanged(slot);
    }

    @Override
    public void onContentsChanged(int slot) {
        updateCounts();
        cabinet.markDirty();
    }

    @Override
    public void onLoad() {
        updateCounts();
    }

    public void updateCounts() {
        this.itemCount = 0;
        this.usedSlotCount = 0;
        ItemStack firstStack = null;
        for (ItemStack stack : stacks) {
            if (stack != null && stack.stackSize > 0) {
                usedSlotCount++;
                itemCount += stack.stackSize;
                if (firstStack == null) firstStack = stack;
            }
        }
        itemMatcher = (firstStack != null) ? cabinet.extractMatcher(firstStack) : null;
    }

    public int getStoredQuantity() {
        return itemCount;
    }

    public int getCapacity() {
        return baseCapacity + capacityPerUpgrade * cabinet.getNumberOfUpgrades();
    }

    public int getSlotsUsed() {
        return usedSlotCount;
    }

    public int getRemainingCapacity() {
        return getCapacity() - getStoredQuantity();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        boolean isItemNotNull = stack != null && stack.stackSize > 0;
        if (!isItemNotNull) return false;
        boolean itemMatches = itemMatcher == null || itemMatcher.test(stack);
        if (!itemMatches) return false;
        int remainingCapacity = getRemainingCapacity();
        if (remainingCapacity <= 0) return false;

        return cabinet.isItemAllowed(stack);
    }
}
