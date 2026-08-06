package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import cofh.api.energy.IEnergyHandler;
import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.EnergyTransferNodeLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityEnergyTransferNode extends TileEntityTransferNodeBase<EnergyTransferNodeLogic>
    implements IGuiHolder<PosGuiData>, IWalkingComponent<Integer>, IEnergyHandler
{

    public TileEntityEnergyTransferNode() {}

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
    public void onChunkUnload() {
        super.onChunkUnload();
        getLogic().resetUpgrades();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        getLogic().resetUpgrades();
    }

    @Override
    public Integer getWalkingObject() {
        return getLogic().containedEnergy;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings)
    {
        return getLogic().buildUI(data, syncManager, settings);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return getLogic().createScreen(data, mainPanel);
    }

    @Override
    protected EnergyTransferNodeLogic getLogic()
    {
        if (logic == null)
        {
            logic = new EnergyTransferNodeLogic(this);
        }
        return logic;
    }

    @Override
    public int receiveEnergy(ForgeDirection forgeDirection, int i, boolean b) {
        return getLogic().receiveEnergy(forgeDirection, i, b);
    }

    @Override
    public int extractEnergy(ForgeDirection forgeDirection, int i, boolean b) {
        return getLogic().extractEnergy(forgeDirection, i, b);
    }

    @Override
    public int getEnergyStored(ForgeDirection forgeDirection) {
        return getLogic().getEnergyStored(forgeDirection);
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection forgeDirection) {
        return getLogic().getMaxEnergyStored(forgeDirection);
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection forgeDirection) {
        return getLogic().canConnectEnergy(forgeDirection);
    }
}
