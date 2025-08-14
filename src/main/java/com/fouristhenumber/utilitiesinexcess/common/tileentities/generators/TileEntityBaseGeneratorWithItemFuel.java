package com.fouristhenumber.utilitiesinexcess.common.tileentities.generators;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.InvWrapper;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;

public abstract class TileEntityBaseGeneratorWithItemFuel extends TileEntityBaseGenerator {

    protected ItemStack fuelStack = null;

    public TileEntityBaseGeneratorWithItemFuel(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    /** Override to define how much RF per tick a given fuel stack provides. */
    protected abstract int getRFPerTick(ItemStack stack);

    /** Override to define burn time for a given fuel stack. */
    protected abstract int getFuelBurnTime(ItemStack stack);

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        fuelStack = stack;
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
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return fuelStack;
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        if (fuelStack != null) {
            ItemStack stack;
            if (fuelStack.stackSize <= count) {
                stack = fuelStack;
                fuelStack = null;
            } else {
                stack = fuelStack.splitStack(count);
                if (fuelStack.stackSize <= 0) {
                    fuelStack = null;
                }
            }
            return stack;
        }
        return null;
    }

    @Override
    protected boolean consumeFuel() {
        if (fuelStack != null && fuelStack.getItem() != null
            && energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored()) {
            currentFuelBurnTime = getFuelBurnTime(fuelStack);
            currentRFPerTick = getRFPerTick(fuelStack);
            fuelStack.stackSize--;
            if (fuelStack.stackSize <= 0) {
                fuelStack = fuelStack.getItem()
                    .getContainerItem(fuelStack);
            }
            return true;
        }
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("FuelStack")) {
            fuelStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("FuelStack"));
        } else {
            fuelStack = null;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (fuelStack != null) {
            NBTTagCompound fuelStackTag = new NBTTagCompound();
            fuelStack.writeToNBT(fuelStackTag);
            tag.setTag("FuelStack", fuelStackTag);
        }
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = super.buildUI(data, syncManager, settings);

        SlotGroup slotGroup = new SlotGroup("fuel_slot", 1);

        IItemHandler itemHandler = new InvWrapper(this);
        ModularSlot slot = new ModularSlot(itemHandler, 0).slotGroup(slotGroup);

        panel.child(
            new Grid().coverChildren()
                .pos(79, 34)
                .mapTo(1, 1, index -> new ItemSlot().slot(slot)));

        return panel;
    }
}
