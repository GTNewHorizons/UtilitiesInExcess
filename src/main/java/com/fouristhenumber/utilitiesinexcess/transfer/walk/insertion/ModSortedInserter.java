package com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion;

import cpw.mods.fml.common.registry.GameRegistry;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

public class ModSortedInserter extends BaseInserter
{
    @Override
    public ItemStack TryInsertItem(IInventory inventory, ItemStack stack)
    {
        if (stack == null || stack.stackSize <= 0)
        {
            return null;
        }

        boolean empty = true;
        boolean modItemMatched = false;
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
                if (!modItemMatched && existing.getItem() == stack.getItem() &&
                    existing.getItemDamage() == stack.getItemDamage())
                {
                    GameRegistry.UniqueIdentifier idA = GameRegistry.findUniqueIdentifierFor(stack.getItem());
                    GameRegistry.UniqueIdentifier idB = GameRegistry.findUniqueIdentifierFor(existing.getItem());

                    if (idA != null && idB != null)
                    {
                        if (idA.modId.equals(idB.modId))
                        {
                            modItemMatched = true;
                        }
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

        if (empty || modItemMatched)
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
        boolean modItemMatched = false;
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
                if (!modItemMatched && existing.getItem() == stack.getItem() &&
                    existing.getItemDamage() == stack.getItemDamage())
                {
                    GameRegistry.UniqueIdentifier idA = GameRegistry.findUniqueIdentifierFor(stack.getItem());
                    GameRegistry.UniqueIdentifier idB = GameRegistry.findUniqueIdentifierFor(existing.getItem());

                    if (idA != null && idB != null)
                    {
                        if (idA.modId.equals(idB.modId))
                        {
                            modItemMatched = true;
                        }
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
        if (empty || modItemMatched)
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
