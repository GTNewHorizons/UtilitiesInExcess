package com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion;

import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.TargetResolver;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

public class RationedInserter extends BaseInserter
{
    int limit;
    public RationedInserter(int limit)
    {
        this.limit = limit;
    }

    // Insertion logic for non-sided inventories where there's a maxAllowed in the inventory
    @Override
    public ItemStack TryInsertItem(IInventory inventory, ItemStack stack)
    {
        if (stack == null || stack.stackSize <= 0)
        {
            return null;
        }

        int size = inventory.getSizeInventory();

        // I'm using IntArrayLists to be the fastest.
        // Even entries are slot numbers and subsequent odd entries are the amount of items that can be put into them.
        IntArrayList mergeableSlots = new IntArrayList();
        IntArrayList emptySlots = new IntArrayList();

        int found = 0;

        for (int slot = 0; slot < size; slot++)
        {
            if (!inventory.isItemValidForSlot(slot, stack))
            {
                continue;
            }

            ItemStack existing = inventory.getStackInSlot(slot);
            if (existing == null)
            {
                emptySlots.add(slot);
                emptySlots.add(inventory.getInventoryStackLimit());
            }
            else if (canStacksMerge(existing, stack))
            {
                found += existing.stackSize;
                if (found >= limit)
                {
                    return stack;
                }

                int max = Math.min(existing.getMaxStackSize(), inventory.getInventoryStackLimit());
                int space = max - existing.stackSize;
                if (space > 0)
                {
                    mergeableSlots.add(slot);
                    mergeableSlots.add(space);
                }
            }
        }

        int preMergeStackSize = stack.stackSize;
        // Once for mergeable slots first, then the empty slots.
        StackToInventoryMergingHelperLimited(mergeableSlots, inventory, stack, found);
        if (stack.stackSize <= 0)
        {
            return null;
        }
        found += preMergeStackSize - stack.stackSize;
        return StackToInventoryMergingHelperLimited(emptySlots, inventory, stack, found);
    }

    // Insertion logic for sided inventories where there's a maxAllowed in the inventory
    @Override
    public ItemStack TryInsertItemSided(ISidedInventory inventory, ItemStack stack, int side)
    {
        if (stack == null || stack.stackSize <= 0)
        {
            return null;
        }

        int[] slots = inventory.getAccessibleSlotsFromSide(side);

        // I'm using IntArrayLists to be the fastest.
        // Even entries are slot numbers and subsequent odd entries are the amount of items that can be put into them.
        IntArrayList mergeableSlots = new IntArrayList();
        IntArrayList emptySlots = new IntArrayList();

        int found = 0;
        for (int slot : slots)
        {
            if (!inventory.canInsertItem(slot, stack, side))
            {
                continue;
            }

            ItemStack existing = inventory.getStackInSlot(slot);
            if (existing == null)
            {
                emptySlots.add(slot);
                emptySlots.add(inventory.getInventoryStackLimit());
            }
            else if (canStacksMerge(existing, stack))
            {
                found += existing.stackSize;
                if (found >= limit)
                {
                    return stack;
                }

                int max = Math.min(existing.getMaxStackSize(), inventory.getInventoryStackLimit());
                int space = max - existing.stackSize;
                if (space > 0)
                {
                    mergeableSlots.add(slot);
                    mergeableSlots.add(space);
                }
            }
        }

        // Once for mergeable slots first, then the empty slots.
        int preMergeStackSize = stack.stackSize;
        StackToInventoryMergingHelperLimited(mergeableSlots, inventory, stack, found);
        if (stack.stackSize <= 0)
        {
            return null;
        }
        found += preMergeStackSize - stack.stackSize;
        return StackToInventoryMergingHelperLimited(emptySlots, inventory, stack, found);
    }

    // Helper for consistent inventory insertion/merging
    private ItemStack StackToInventoryMergingHelperLimited(
        IntArrayList slotInfo,
        IInventory inventory,
        ItemStack insertionStack,
        int currentItemsInInventory)
    {
        for (int i = 0; i < slotInfo.size(); i += 2)
        {
            int insertAmount = limit - currentItemsInInventory;
            if (insertAmount <= 0)
            {
                return insertionStack.stackSize <= 0 ? null : insertionStack;
            }

            int slot = slotInfo.getInt(i);

            int amountInsertable = Math.min(
                slotInfo.getInt(i + 1),
                Math.min(insertionStack.stackSize, insertAmount)
            );

            if (amountInsertable <= 0)
            {
                continue;
            }

            insertionStack.stackSize -= amountInsertable;
            currentItemsInInventory += amountInsertable;

            ItemStack existing = inventory.getStackInSlot(slot);

            if (existing == null)
            {
                ItemStack newStack = insertionStack.copy();
                newStack.stackSize = amountInsertable;
                inventory.setInventorySlotContents(slot, newStack);
            }
            else
            {
                existing.stackSize += amountInsertable;
            }

            if (insertionStack.stackSize <= 0)
            {
                return null;
            }
        }

        return insertionStack;
    }

}
