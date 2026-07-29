package com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

public class SortedInserter extends BaseInserter
{
    @Override
    public ItemStack TryInsertItem(IInventory inventory, ItemStack stack)
    {
        if (stack == null || stack.stackSize <= 0)
        {
            return null;
        }

        boolean empty = true;
        boolean itemMatched = false;
        int size = inventory.getSizeInventory();

        // I'm using IntArrayLists to be the fastest.
        // Even entries are slot numbers and subsequent odd entries are the amount of items that can be put into them.
        IntArrayList mergeableSlots = new IntArrayList();
        IntArrayList emptySlots = new IntArrayList();

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
            else
            {
                empty = false;
                if (!itemMatched && existing.getItem() == stack.getItem() &&
                    existing.getItemDamage() == stack.getItemDamage())
                {
                    if (ItemStack.areItemStackTagsEqual(existing, stack))
                    {
                        itemMatched = true;
                    }
                }
            }

            if (existing != null && canStacksMerge(existing, stack))
            {
                int max = Math.min(existing.getMaxStackSize(), inventory.getInventoryStackLimit());
                int space = max - existing.stackSize;
                if (space > 0)
                {
                    mergeableSlots.add(slot);
                    mergeableSlots.add(space);
                }
            }
        }

        if (empty || itemMatched)
        {
            StackToInventoryMergingHelper(mergeableSlots, inventory, stack);
            if (stack.stackSize <= 0)
            {
                return null;
            }
            return StackToInventoryMergingHelper(emptySlots, inventory, stack);
        }
        return stack;
    }

    @Override
    public ItemStack TryInsertItemSided(ISidedInventory inventory, ItemStack stack, int side)
    {
        if (stack == null || stack.stackSize <= 0)
        {
            return null;
        }

        boolean empty = true;
        boolean itemMatched = false;
        int[] slots = inventory.getAccessibleSlotsFromSide(side);

        IntArrayList mergeableSlots = new IntArrayList();
        IntArrayList emptySlots = new IntArrayList();

        // Note that we have to iterate the whole inventory first or we won't know if there's mergable slots
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
            else
            {
                empty = false;
                if (!itemMatched && existing.getItem() == stack.getItem() &&
                    existing.getItemDamage() == stack.getItemDamage())
                {
                    if (ItemStack.areItemStackTagsEqual(existing, stack))
                    {
                        itemMatched = true;
                    }
                }
            }

            if (existing != null && canStacksMerge(existing, stack))
            {
                int max = Math.min(existing.getMaxStackSize(), inventory.getInventoryStackLimit());
                int space = max - existing.stackSize;
                if (space > 0)
                {
                    mergeableSlots.add(slot);
                    mergeableSlots.add(space);
                }
            }
        }
        if (empty || itemMatched)
        {
            StackToInventoryMergingHelper(mergeableSlots, inventory, stack);
            if (stack.stackSize <= 0) {
                return null;
            }
            return StackToInventoryMergingHelper(emptySlots, inventory, stack);
        }
        return stack;
    }
}
