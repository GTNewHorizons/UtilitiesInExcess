package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.FluidRetrievalNodeLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.ItemRetrievalNodeLogic;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityItemRetrievalNode extends TileEntityTransferNodeBase<ItemRetrievalNodeLogic>
    implements IGuiHolder<PosGuiData>, IWalkingComponent<ItemStack>
{
    public TileEntityItemRetrievalNode() {}

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
    public ItemStack getWalkingObject() {
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
    protected ItemRetrievalNodeLogic getLogic()
    {
        if (logic == null)
        {
            logic = new ItemRetrievalNodeLogic(this);
        }
        return logic;
    }
}
