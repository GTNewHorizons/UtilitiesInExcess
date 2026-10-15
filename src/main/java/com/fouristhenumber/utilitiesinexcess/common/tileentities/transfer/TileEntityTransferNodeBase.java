package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockNodeBase;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.BaseNodeLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.INodeLogicHost;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.NetworkLogic;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class TileEntityTransferNodeBase extends TileEntityNetworkComponentBase
    implements INodeLogicHost, IGuiHolder<PosGuiData>, IWalkingComponent {

    NetworkLogic logic;

    public abstract NetworkLogic getLogic();

    @Override
    public void updateEntity() {
        ((BaseNodeLogic) this.getLogic()).updateEntity();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        this.getLogic()
            .writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.getLogic()
            .readFromNBT(nbt);
    }

    @Override
    public Object getWalkingObject() {
        return ((BaseNodeLogic) getLogic()).getWalkingObject();
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
    public ForgeDirection getFacing() {
        return BlockNodeBase.getFacing(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
    }

    public void dropInventoryContents() {
        getLogic().dropContents(worldObj, xCoord, yCoord, zCoord);
    }

    @Override
    public void invalidateInventory() {
        getLogic().invalidateInventory();
    }
}
