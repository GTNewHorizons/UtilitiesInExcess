package com.fouristhenumber.utilitiesinexcess.utils;

import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.utils.item.INBTSerializable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class VoidItemHandler implements IItemHandler, INBTSerializable<NBTTagCompound> {

    int size;

    public VoidItemHandler(int size) {
        this.size = size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    /*
    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
    }*/

    @Override
    public int getSlots() {
        return size;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return null;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return null;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return null;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("Size", this.size);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.setSize(nbt.hasKey("Size", 3) ? nbt.getInteger("Size") : this.size);
    }

    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= this.size) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + this.size + ")");
        }
    }

    protected void onLoad() {}

    protected void onContentsChanged(int slot) {}
}
