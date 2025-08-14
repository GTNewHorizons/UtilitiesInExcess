package com.fouristhenumber.utilitiesinexcess.common.tileentities.generators;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class TileEntityFurnaceGenerator extends TileEntityBaseGenerator {

    private final ItemStack[] inventory = new ItemStack[1]; // Single fuel slot

    public TileEntityFurnaceGenerator() {
        super(100000, 200); // 100k RF storage, 200 RF/t max transfer
    }

    @Override
    protected int getRFPerTick(ItemStack currentBurningItem) {
        return 40;
    }

    @Override
    protected int getFuelBurnTime(ItemStack stack) {
        return TileEntityFurnace.getItemBurnTime(stack);
    }

    @Override
    protected ItemStack consumeFuel() {
        if (inventory[0] != null && inventory[0].getItem() != null) {
            inventory[0].stackSize--;
            if (inventory[0].stackSize <= 0) {
                inventory[0] = inventory[0].getItem()
                    .getContainerItem(inventory[0]);
            }
        }
        return inventory[0];
    }

    @Override
    protected ItemStack getFuelStack() {
        return inventory[0];
    }

    @Override
    public int getSizeInventory() {
        return inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        if (inventory[slot] != null) {
            ItemStack stack;
            if (inventory[slot].stackSize <= count) {
                stack = inventory[slot];
                inventory[slot] = null;
            } else {
                stack = inventory[slot].splitStack(count);
                if (inventory[slot].stackSize <= 0) {
                    inventory[slot] = null;
                }
            }
            return stack;
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        inventory[slot] = stack;
    }

    @Override
    public String getInventoryName() {
        return "tile.furnace_generator.name";
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
    public boolean isUseableByPlayer(net.minecraft.entity.player.EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return TileEntityFurnace.isItemFuel(stack);
    }
}
