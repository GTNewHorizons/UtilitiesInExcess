package com.fouristhenumber.utilitiesinexcess.transfer.upgrade;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import com.cleanroommc.modularui.widgets.slot.IOnSlotChanged;
import com.fouristhenumber.utilitiesinexcess.common.items.ItemUpgrade;
import com.fouristhenumber.utilitiesinexcess.utils.ItemStackInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

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

    public void init()
    {
        for (ItemStack stack : upgrades)
        {
            TransferUpgrade upgrade = TransferUpgrade.getUpgrade(stack);
            if (upgrade != null)
            {
                upgrade.applyTo(host, stack);
            }
        }
    }
    public void writeToNBT(NBTTagCompound nbt)
    {
        NBTTagList itemTagList = new NBTTagList();

        for (int i = 0; i < this.upgrades.length; ++i)
        {
            if (this.upgrades[i] != null)
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                this.upgrades[i].writeToNBT(nbttagcompound);
                itemTagList.appendTag(nbttagcompound);
            }
        }

        nbt.setTag("Upgrades", itemTagList);
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
        NBTTagList nbttaglist = nbt.getTagList("Upgrades", 10);
        this.upgrades = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound compound = nbttaglist.getCompoundTagAt(i);
            int slot = compound.getByte("Slot") & 255;

            if (slot < this.upgrades.length)
            {
                this.upgrades[slot] = ItemStack.loadItemStackFromNBT(compound);
            }
        }
    }


    public void writeDesc(MCDataOutput output) {
        for (ItemStack upgrade : this.upgrades) {
            output.writeItemStack(upgrade);
        }
    }

    public void readDesc(MCDataInput input) {
        for (int i = 0; i < this.upgrades.length; i++) {
            this.upgrades[i] = input.readItemStack();
        }
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
        return ItemStackInventory.decrStackSizeInItemStackArray(index, count, upgrades, this);
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
