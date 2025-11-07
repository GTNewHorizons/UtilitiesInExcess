package com.fouristhenumber.utilitiesinexcess.transfer.upgrade;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import com.fouristhenumber.utilitiesinexcess.transfer.filter.ITransferFilter;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.ITransferWalker;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.StandardWalker;

public class UpgradeInventory implements IInventory {

    private final List<UpgradeData> inv;

    public UpgradeInventory(int slots) {
        this.inv = Stream.generate(UpgradeData::new)
            .limit(slots)
            .collect(Collectors.toList());
    }

    public ITransferWalker getWalker() {
        TransferUpgrade upgrade = getWalkerUpgrade();
        return upgrade == null ? new StandardWalker() : upgrade.getWalker();
    }

    private TransferUpgrade getWalkerUpgrade() {
        return inv.stream()
            .map(UpgradeData::getUpgrade)
            .filter(TransferUpgrade::isWalkerUpgrade)
            .findAny()
            .orElse(null);
    }

    @Nullable
    public ITransferFilter getFilter() {
        return null; // todo
    }

    // IInventory

    @Override
    public int getSizeInventory() {
        return this.inv.size();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        if (index < 0) return null;
        if (index > this.inv.size()) return null;

        return this.inv.get(index)
            .getStack();
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (index < 0) return null;
        if (index > this.inv.size()) return null;

        UpgradeData data = this.inv.get(index);
        if (data == null) return null;

        ItemStack stack = data.getStack();
        int toMove = Math.min(stack.stackSize, count);
        ItemStack newStack = stack.copy();
        newStack.stackSize = toMove;
        stack.stackSize -= toMove;
        if (stack.stackSize == 0) {
            data.clear();
        }
        markDirty();
        return newStack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index < 0) return;
        if (index > this.inv.size()) return;

        UpgradeData data = this.inv.get(index);
        data.set(stack);
        markDirty();
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (stack == null) return true; // allow stuff to be removed
        TransferUpgrade upgrade = TransferUpgrade.getUpgrade(stack);
        if (upgrade == null) return false;

        // Handle special cases
        int walkerIndex = -1;
        int filterIndex = -1;
        for (int i = 0; i < this.inv.size(); i++) {
            UpgradeData data = this.inv.get(i);
            if (data.isEmpty()) continue;

            if (data.upgrade.isWalkerUpgrade()) walkerIndex = i;
            if (data.upgrade.isFilterUpgrade()) filterIndex = i;
        }

        if (upgrade.isWalkerUpgrade()) return walkerIndex == -1 || walkerIndex == index;
        if (upgrade.isFilterUpgrade()) return walkerIndex == -1 || filterIndex == index;
        return true;
    }

    @Override
    public void markDirty() {
        // todo
    }

    @Override
    public String getInventoryName() {
        return "";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    // IInventory - Unused for this inventory

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return null;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    private static class UpgradeData {

        private ItemStack stack;
        private TransferUpgrade upgrade;

        public void set(ItemStack stack) {
            this.stack = stack;
            this.upgrade = TransferUpgrade.getUpgrade(stack);
        }

        public ItemStack getStack() {
            return this.stack;
        }

        public TransferUpgrade getUpgrade() {
            return this.upgrade;
        }

        public boolean isEmpty() {
            return this.stack == null;
        }

        public void clear() {
            this.stack = null;
            this.upgrade = null;
        }
    }
}
