package com.fouristhenumber.utilitiesinexcess.common.tileentities.generators;

import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.redstoneGeneratorFuelBurnTime;
import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.redstoneGeneratorRFCapacity;
import static com.fouristhenumber.utilitiesinexcess.config.blocks.GeneratorConfig.redstoneGeneratorRFPerTick;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
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

public class TileEntityRedstoneGenerator extends TileEntityLavaGenerator implements IInventory {

    public TileEntityRedstoneGenerator() {
        super(redstoneGeneratorRFCapacity);
    }

    protected ItemStack redstoneStack = null;

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (stack == null) return false;
        return stack.getItem() == Items.redstone;
    }

    @Override
    protected boolean consumeFuel() {
        if (fluid != null && energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored()
            && fluid.amount >= 125
            && redstoneStack != null
            && redstoneStack.getItem() != null) {
            currentFuelBurnTime = redstoneGeneratorFuelBurnTime;
            currentRFPerTick = redstoneGeneratorRFPerTick;
            fluidTank.drain(125, true);
            redstoneStack.stackSize--;
            if (redstoneStack.stackSize <= 0) redstoneStack = null;
            return true;
        }
        return false;
    }

    @Override
    protected String getGUIName() {
        return getInventoryName();
    }

    @Override
    public String getInventoryName() {
        return "tile.redstone_generator.name";
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        redstoneStack = stack;
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
        return redstoneStack;
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        if (redstoneStack != null) {
            ItemStack stack;
            if (redstoneStack.stackSize <= count) {
                stack = redstoneStack;
                redstoneStack = null;
            } else {
                stack = redstoneStack.splitStack(count);
                if (redstoneStack.stackSize <= 0) {
                    redstoneStack = null;
                }
            }
            return stack;
        }
        return null;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("RedstoneStack")) {
            redstoneStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("RedstoneStack"));
        } else {
            redstoneStack = null;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (redstoneStack != null) {
            NBTTagCompound fuelStackTag = new NBTTagCompound();
            redstoneStack.writeToNBT(fuelStackTag);
            tag.setTag("RedstoneStack", fuelStackTag);
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
                .pos(79, 52)
                .mapTo(1, 1, index -> new ItemSlot().slot(slot)));

        return panel;
    }
}
