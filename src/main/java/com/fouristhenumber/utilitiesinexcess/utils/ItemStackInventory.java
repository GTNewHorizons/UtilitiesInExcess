package com.fouristhenumber.utilitiesinexcess.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.List;

public class ItemStackInventory implements IInventory
{
    private final int size;
    private final ItemStack[] filters;
    private final int stackLimit;
    private boolean dirty = false;

    public ItemStackInventory(ItemStack stack, int size, int stackLimit)
    {
        this.stackLimit = stackLimit;
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null)
        {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }

        this.size = nbt.hasKey("size") ? nbt.getInteger("size") : size;
        this.filters = new ItemStack[this.size];

        NBTTagList list = nbt.getTagList("Items", 10);

        for (int i = 0; i < list.tagCount(); i++)
        {
            NBTTagCompound itemTag = list.getCompoundTagAt(i);

            int slot = itemTag.getByte("Slot") & 255;

            if (slot >= 0 && slot < this.size)
            {
                filters[slot] = ItemStack.loadItemStackFromNBT(itemTag);
            }
        }
    }

    public void writeInventoryToHeldStack(EntityPlayer player)
    {
        if (!dirty || player.getEntityWorld().isRemote)
        {
            return;
        }
        ItemStack heldStack = player.getHeldItem();
        if (heldStack == null)
        {
            return; // This can happen if you don't lock the slot the inventory is from.
        }
        if (heldStack.stackTagCompound == null)
        {
            heldStack.stackTagCompound = new NBTTagCompound();
        }
        NBTTagCompound compound = heldStack.stackTagCompound;
        compound.setInteger("size", filters.length);
        NBTTagList itemList = new NBTTagList();
        for (int i = 0; i < filters.length; i++)
        {
            ItemStack stack = filters[i];
            if (stack != null)
            {
                NBTTagCompound itemCompound = new NBTTagCompound();
                itemCompound.setByte("Slot", (byte) i);
                stack.writeToNBT(itemCompound);
                itemList.appendTag(itemCompound);
            }
        }
        compound.setTag("Items", itemList);
    }

    public static List<ItemStack> getInventoryContentsFromStack(ItemStack itemInventory)
    {
        if (itemInventory == null || !itemInventory.hasTagCompound())
        {
            return null;
        }

        NBTTagCompound compound = itemInventory.stackTagCompound;

        if (!compound.hasKey("Items"))
        {
            return null;
        }

        int size = compound.getInteger("size");
        List<ItemStack> contents = new ArrayList<ItemStack>(size);

        NBTTagList itemList = compound.getTagList("Items", 10); // 10 = NBTTagCompound

        for (int i = 0; i < itemList.tagCount(); i++)
        {
            NBTTagCompound itemCompound = itemList.getCompoundTagAt(i);
            contents.add(ItemStack.loadItemStackFromNBT(itemCompound));
        }

        return contents;
    }

    @Override
    public int getSizeInventory() {
        return size;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return filters[slotIn];
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        return ItemStackInventory.decrStackSizeInItemStackArray(index, count, filters, this);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index)
    {
        return filters[index];
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        filters[index] = stack;
        markDirty();
    }

    @Override
    public String getInventoryName() {
        return "upgrade_inventory";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return stackLimit;
    }

    @Override
    public void markDirty() {
        this.dirty = true;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    public static ItemStack decrStackSizeInItemStackArray(int index, int count, ItemStack[] arr, IInventory inv)
    {
        if (arr[index] == null)
        {
            return null;
        }

        ItemStack itemstack;
        if (arr[index].stackSize <= count)
        {
            itemstack = arr[index];
            arr[index] = null;
        }
        else
        {
            itemstack = arr[index].splitStack(count);

            if (arr[index].stackSize == 0)
            {
                arr[index] = null;
            }
        }

        if (inv != null)
        {
            inv.markDirty();
        }
        return itemstack;
    }

    public static ItemStack decrStackSize(int count, IInventory inv)
    {
        ItemStack item = inv.getStackInSlot(0);

        if (item == null)
        {
            return null;
        }

        ItemStack result;

        if (item.stackSize <= count)
        {
            result = item;
            inv.setInventorySlotContents(0, null);
        }
        else
        {
            result = item.splitStack(count);

            if (item.stackSize == 0)
            {
                inv.setInventorySlotContents(0, null);
            }
        }

        inv.markDirty();
        return result;
    }

}
