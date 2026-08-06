package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import com.cleanroommc.modularui.screen.ModularScreen;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.ItemRetrievalNodeLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.ItemTransferNodeLogic;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;


public class TileEntityItemTransferNode extends TileEntityTransferNodeBase<ItemTransferNodeLogic>
    implements IGuiHolder<PosGuiData>, IWalkingComponent<ItemStack>
{
    public TileEntityItemTransferNode() {}

    @Override
    public void updateEntity()
    {
        this.getLogic().updateEntity();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        this.getLogic().writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.getLogic().readFromNBT(nbt);
    }

    @Override
    public ItemStack getWalkingObject()
    {
        return getLogic().getStackInSlot(0);
    }

    @Override
    public ModularPanel buildUI(PosGuiData posGuiData, PanelSyncManager panelSyncManager, UISettings uiSettings) {
        return getLogic().buildUI(posGuiData, panelSyncManager, uiSettings);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return getLogic().createScreen(data, mainPanel);
    }

    @Override
    protected ItemTransferNodeLogic getLogic()
    {
        if (logic == null)
        {
            logic = new ItemTransferNodeLogic(this);
        }
        return logic;
    }
}
