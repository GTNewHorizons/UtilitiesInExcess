package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.FilterPipeLogic;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityFilterPipe extends TileEntityNetworkComponentBase implements IGuiHolder<PosGuiData> {

    private FilterPipeLogic logic;

    public FilterPipeLogic getLogic() {
        if (logic == null) {
            logic = new FilterPipeLogic(this);
        }
        return logic;
    }

    public void invalidateInventory() {
        getLogic().invalidateInventory();
    }

    public TileEntityFilterPipe() {}

    public int getValidMask(ForgeDirection fromDirection, ItemStack stack) {
        return getLogic().getValidMask(fromDirection, stack);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        getLogic().readFromNBT(compound);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        getLogic().writeToNBT(compound);
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        return getLogic().buildUI(data, syncManager, settings);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return getLogic().createScreen(data, mainPanel);
    }

}
