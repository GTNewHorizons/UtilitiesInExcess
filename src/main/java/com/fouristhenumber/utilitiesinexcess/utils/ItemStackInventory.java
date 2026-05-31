package com.fouristhenumber.utilitiesinexcess.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ItemStackInventory implements IInventory
{
    private final int size;
    private final ItemStack[] filters;


    private boolean dirty = false;

    public ItemStackInventory(ItemStack stack)
    {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null)
        {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }

        this.size = nbt.hasKey("size") ? nbt.getInteger("size") : 9;
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
        ItemStack heldStack = player.getHeldItem();
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

    private ItemStackInventory(int size)
    {
        this.size = size;
        this.filters = new ItemStack[size];
    }

    public static ItemStackInventory BlankInventory(ItemStack container)
    {
        if (container.getItem() instanceof ItemStackInventoryContainer inventoryContainer)
        {
            return new ItemStackInventory(inventoryContainer.getInventorySize());
        }
        throw new IllegalArgumentException("Blank inventory attempted to be created on an item that is not an ItemContainer");
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
        if (filters[index] == null)
        {
            return null;
        }
        markDirty();
        return filters[index].splitStack(count);
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
        return 64;
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
}
