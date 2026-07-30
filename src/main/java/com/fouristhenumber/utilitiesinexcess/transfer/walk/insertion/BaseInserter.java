package com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion;

import com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting.TargetResolver;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

public abstract class BaseInserter
{
    public ItemStack insert(TargetResolver.Target<IInventory> target, ItemStack stack)
    {
        if (target.handler instanceof ISidedInventory sidedInventory) // Sided logic
        {
            return TryInsertItemSided(sidedInventory, stack, target.side);
        }
        else // Basic logic
        {
            return TryInsertItem(target.handler, stack);
        }
    }

    protected abstract ItemStack TryInsertItem(IInventory inventory, ItemStack stack);

    protected abstract ItemStack TryInsertItemSided(ISidedInventory inventory, ItemStack stack, int side);


    public boolean canStacksMerge(ItemStack a, ItemStack b)
    {
        return a.getItem() == b.getItem()
            && a.getItemDamage() == b.getItemDamage()
            && ItemStack.areItemStackTagsEqual(a, b);
    }

    public ItemStack StackToInventoryMergingHelper(IntArrayList slotInfo, IInventory inventory, ItemStack insertionStack)
    {
        for (int i = 0; i < slotInfo.size(); i += 2)
        {
            int slot = slotInfo.getInt(i);
            int amountInsertable = Math.min(slotInfo.getInt(i + 1), insertionStack.stackSize);
            insertionStack.stackSize -= amountInsertable;
            if (inventory.getStackInSlot(slot) == null)
            {
                ItemStack newStack = insertionStack.copy();
                newStack.stackSize = amountInsertable;
                inventory.setInventorySlotContents(slot, newStack);
            }
            else
            {
                inventory.getStackInSlot(slot).stackSize += amountInsertable;
            }

            if (insertionStack.stackSize <= 0)
            {
                return null;
            }
        }
        return insertionStack;
    }
}
