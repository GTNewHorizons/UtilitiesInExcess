package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

public class TileEntityMarginallyMaximisedChest extends TileEntity implements IInventory {

    private final ItemStack[] chestContents = new ItemStack[getSizeInventory()];

    @Override
    public int getSizeInventory() {
        return 27;
    }

    @Override
    public String getInventoryName() {
        return "tile.marginally_maximised_chest.name";
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return this.chestContents[slotIn];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (this.chestContents[index] != null) {
            ItemStack itemstack;

            if (this.chestContents[index].stackSize <= count) {
                itemstack = this.chestContents[index];
                this.chestContents[index] = null;
                this.markDirty();
                return itemstack;
            } else {
                itemstack = this.chestContents[index].splitStack(count);

                if (this.chestContents[index].stackSize == 0) {
                    this.chestContents[index] = null;
                }

                this.markDirty();
                return itemstack;
            }
        } else {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        if (this.chestContents[index] != null) {
            ItemStack itemstack = this.chestContents[index];
            this.chestContents[index] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.chestContents[index] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }

        this.markDirty();
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
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && player
            .getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D)
            <= 64.0D;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        NBTTagList nbttaglist = nbttagcompound.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        for (int index = 0; index < nbttaglist.tagCount(); index++) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(index);
            int slot = nbttagcompound1.getByte("Slot") & 255;
            if (slot >= 0 && slot < getSizeInventory()) {
                setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(nbttagcompound1));
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        NBTTagList nbttaglist = new NBTTagList();
        for (int index = 0; index < getSizeInventory(); index++) {
            ItemStack curStack = getStackInSlot(index);
            if (curStack != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte) index);
                curStack.writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }
        nbttagcompound.setTag("Items", nbttaglist);
    }
}
