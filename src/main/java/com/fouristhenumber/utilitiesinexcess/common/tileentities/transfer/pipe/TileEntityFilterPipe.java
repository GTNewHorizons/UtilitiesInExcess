package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityNetworkComponentBase;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.pipe.FilterPipeLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityFilterPipe extends TileEntityNetworkComponentBase<FilterPipeLogic>
    implements IGuiHolder<PosGuiData>, IInventory
{

    public TileEntityFilterPipe()
    {
        logic = new FilterPipeLogic(this);
    }

    @Override
    public boolean canConnectToSide(ForgeDirection side) {
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings)
    {
        return logic.buildUI(data, syncManager, settings, this);
    }

    @Override
    public int getSizeInventory() {
        return logic.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return logic.getStackInSlot(slotIn);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return logic.decrStackSize(index, count);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return logic.getStackInSlotOnClosing(index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        logic.setInventorySlotContents(index, stack);
    }

    @Override
    public String getInventoryName() {
        return logic.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return logic.hasCustomInventoryName();
    }

    @Override
    public int getInventoryStackLimit() {
        return logic.getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return logic.isUseableByPlayer(player);
    }

    @Override
    public void openInventory()
    {}

    @Override
    public void closeInventory()
    {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return logic.isItemValidForSlot(index, stack);
    }
}
