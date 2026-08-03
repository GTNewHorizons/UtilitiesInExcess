package com.fouristhenumber.utilitiesinexcess.transfer.upgrade;

import com.cleanroommc.modularui.widgets.slot.IOnSlotChanged;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemUpgrade;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class UpgradeInventory implements IOnSlotChanged, IInventory
{
    ItemStack[] upgrades;
    IUpgradeable host;

    public UpgradeInventory(int size, IUpgradeable node)
    {
        upgrades = new ItemStack[size];
        host = node;
    }

    @Override
    public void onChange(ItemStack newItem, boolean onlyAmountChanged, boolean client, boolean init)
    {
        if (!client && !init)
        {
            host.resetUpgrades();

            for (ItemStack stack : upgrades)
            {
                TransferUpgrade upgrade = TransferUpgrade.getUpgrade(stack);
                if (upgrade != null)
                {
                    upgrade.applyTo(host, stack);
                }
            }
        }
    }

    public void writeToNBT(NBTTagCompound nbt)
    {
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
    }

    @Override
    public int getSizeInventory() {
        return upgrades.length;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn)
    {
        return upgrades[slotIn];
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        if (upgrades[index] == null)
        {
            return null;
        }
        return upgrades[index].splitStack(count);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index)
    {
        return upgrades[index];
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        upgrades[index] = stack;
        this.markDirty();
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
    public void markDirty() {
        host.markDirty();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return stack.getItem() instanceof ItemUpgrade;
    }
}
