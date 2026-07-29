package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.EnergyTransferNodeLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.IWalkingComponent;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityEnergyTransferNode extends TileEntityTransferNodeBase<EnergyTransferNodeLogic>
    implements IGuiHolder<PosGuiData>, IWalkingComponent<Integer>
{

    public TileEntityEnergyTransferNode()
    {
        logic = new EnergyTransferNodeLogic(this);
    }

    public TileEntityEnergyTransferNode(boolean isHyper)
    {
        logic = new EnergyTransferNodeLogic(this);
        logic.hyper = isHyper;
    }

    @Override
    public void updateEntity()
    {
        this.logic.updateEntity();
    }

    @Override
    public void updateSource() {
        // TODO?
    }

    @Override
    protected EnergyTransferNodeLogic createLogic() {
        return new EnergyTransferNodeLogic(this);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        this.logic.writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.logic.readFromNBT(nbt);
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings)
    {
        return logic.buildUI(data, syncManager, settings);
    }

    @Override
    public Integer getWalkingObject() {
        return logic.containedEnergy;
    }
}
